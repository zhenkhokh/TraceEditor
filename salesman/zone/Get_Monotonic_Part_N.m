function bInd = Get_Monotonic_Part_N(startIndex,k,X,Y,ind)

x = X(ind);
is = Get_i_ind(startIndex,ind);
% y = Y(ind);
n = length(x);
d = sign(x(mod(is,n)+1)-x(mod(is-1,n)+1));
bInd = Get_Monotonic_Part(X,Y,ind(is));
m=1;
for i=1:(length(x)-1)
    ii = mod(i+is-1,n)+1;
    ii1 = mod(i+is,n)+1;
%     rBound = bInd(2);
    %bInd = Get_Monotonic_Part(X,Y,ind(ii1));
    %if (m==k && rBound~=bInd(1))
    if(m==k)
        bInd =  Get_Monotonic_Part(X,Y,ind(ii));
        break;
    end
    dCur = sign(x(ii1)-x(ii));
    if (dCur~=d)
        m=m+1;
        d=dCur;
    end
end
end