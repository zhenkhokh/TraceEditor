function m = Get_PartsNumber(X,Y,ind)

x = X(ind);
% y = Y(ind);
n = length(x);
d = sign(x(1)-x(n));
% bInd = Get_Monotonic_Part(X,Y,1);
m=0;%!
for i=1:length(x)-1
    dCur = sign(x(i+1)-x(i));
%     rBound = bInd(2);
%     bInd = Get_Monotonic_Part(X,Y,i+1);
     if (dCur~=d || (dCur==0&&d==0))
%     if(dCur~=d)
        m=m+1;
        d=dCur;
%     else
%         disp(['   ' num2str(m)])
    end
end
end