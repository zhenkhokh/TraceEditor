function belongs = Define_point(x,y,aM,bM,cM,dM,xM,yM,Xmin)
% all parts is x-sorted,except extended points

[ia, ib, ja, jb] = Get_LeftPoints_(x,xM,Xmin);
if (ia(1)==0&&ib(1)==0)
    belongs=false;
    return;
end

% [m, n]=size(xM);
% last=zeros(1,m);
% for i=1:m
%     last(m)=1;% first can be NaN
% 	while(last(m)<=n && ~isnan(xM(i,last(m))))
% 		last(m)=last(m)+1;
% 	end
% 	last(m)=last(m)-1;
% end

cnt=0;
for i=1:length(ia)% !
    jA = ja(i);
    if (jA==0)
        break;
    end
    jB = jb(i);
    iAB = ia(i);
    xA = xM(iAB,jA);
    yA = yM(iAB,jA);
    %if (jB<=1|| jB==last(iAB))
    if(jB<=0)%!
        break;
    end
	xB = xM(iAB,jB);
    yB = yM(iAB,jB);
%     if (xB<xA)
%         tmp=xA;
%         xA = xB;
%         xB=tmp;
%         tmp=yA;
%         yA = yB;
%         yB=tmp;
%     end

	a = aM(iAB,jB);
	b = bM(iAB,jB);
	c = cM(iAB,jB);
	d = dM(iAB,jB);
	cnt = cnt+Count_crossing(x,y,xA,yA,xB,yB,a,b,c,d);
end
if (mod(cnt,2)==0)
	belongs=false;
else
	belongs=true;
end
end