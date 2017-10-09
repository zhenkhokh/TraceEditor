function [a b c d xB pos] = Get_abcd_xB(x,k,xM,aM,bM,cM,dM)
% position -1 - left
% 0 - inner
% 1 - rigth

a=NaN;b=NaN;c=NaN;d=NaN;xB=NaN;
if(length(x)>1)
	error('must be number');
end
isFirstMet = false;
j=2;
while(~isnan(xM(k,j)))
    if (x>xM(k,j-1))
        isFirstMet = true;
    end
	if(x<=xM(k,j) && isFirstMet)
		a = aM(k,j);
		b = bM(k,j);
		c = cM(k,j);
		d = dM(k,j);
		xB = xM(k,j);
        pos = 0;
		return;
    end
    j=j+1;
end
if(isFirstMet)
    pos = 1;
else
    pos = -1;
end
end