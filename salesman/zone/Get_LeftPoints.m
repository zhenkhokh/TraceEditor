function [ia ib ja jb] = Get_LeftPoints(x,xM,Xmin)

[m n] = size(xM);
ia=zeros(1,n);ib=zeros(1,n);ja=zeros(1,n);jb=zeros(1,n);
if (x<Xmin)
	return;% not belong, ia,ib,ja,jb - all zeros
end
% notice xM is sorted on row
nextPart = 1;
for i=1:m
	j = 1;
    isLeftMeet=false;
	while(~isnan(xM(i,j+1)))% more closer use aM
		if (isLeftMeet)
% 			if (x<xM(i,j))
				ib(nextPart) = i;
				jb(nextPart) = j;
				nextPart = nextPart+1;
				isLeftMeet = false;
%             end
        end
% 		if (xM(i,j)<=x)	% ia=ib, because xM[i,end]=xM[i+1,end+1], see Join_Shenberg
			ia(nextPart) = i;
			ja(nextPart) = j;
%         end
        if (isLeftMeet)% if direction is null, next part is ignored if its not null too
			ib(nextPart) = i;
			jb(nextPart) = j;
			nextPart = nextPart+1;
            isLeftMeet = false;
        else
%             if (xM(i,j)<=x)
                isLeftMeet = true;
%             end
        end	
		j=j+1;
    end
end
for i=1:length(ia)
    if (ia(i)~=ib(i) && ib(i)>0)
         error('not possible!');
    end
end
end