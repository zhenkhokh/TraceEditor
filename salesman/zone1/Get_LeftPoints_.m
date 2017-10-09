function [ia, ib, ja, jb] = Get_LeftPoints_(x,xM,Xmin)
    
[m, n] = size(xM);
ia=zeros(1,n);ib=zeros(1,n);ja=zeros(1,n);jb=zeros(1,n);
if (x<Xmin)
	return;% not belong, ia,ib,ja,jb - all zeros
end
nextPoint = 1;
for i=1:m
	isLastB = false;
	offset=0;
	offsetB=0;%!
	last=1;% first can be NaN
	while(last<=n && ~isnan(xM(i,last)))
		last=last+1;
	end
	last=last-1;
%     [iii, jj] = Get_NextB(i,offsetB,last,m, n);
% 	xB = xM(iii,jj);
	while(~isLastB)
		[ii, j] = Get_NextB(i,offset,last,m,n);
		if (ii~=i && ii~=mod(i,m)+1)
			error(['bad i jumping i=' num2str(i) ' ii=' num2str(ii)]);
		end
		xA = xM(i,j);
        %offsetB=offset;%?
		%while(xB<xA)
        while(offsetB<=offset)
			offsetB=offsetB+1; 
			[iii, jj] = Get_NextB(ii,offsetB,last,m,n);
            if( offsetB>=last)%iii~=i ||
				isLastB=true;
                break;%just onece
            else
                xB = xM(iii,jj);
            end
        end
        if (offsetB<offset)
            error('not possible offsetB');
        end
        offset=offset+1;
        if (~isLastB && (xA<x||xB<x))
%             if (offset<offsetB)
                ib(nextPoint)=iii;
                jb(nextPoint)=jj;
%             end
%         end
%             if(xA<x)
                ia(nextPoint)=ii;
                ja(nextPoint)=j;
                nextPoint=nextPoint+1;
%             else
%                 isLastB = true;
%             end
        end
	end
end
for i=1:length(ia)
    if (ia(i)~=ib(i) && ia(i)>0)% && ia(i)>0 &&ib(i)>0)
%          error('not possible!');
        disp(['error ia=' num2str(ia) ' ib=' num2str(ib) ' ja=' num2str(ja) ' jb=' num2str(jb)])
    end
end

