function X = removeYDirection(X,Y,eps)

ind = Get_Monotonic_Part(X,Y,1);
ind = ind(4:end);
n = length(ind);
if (n==4)
    X = prepareSqure(X,Y,2*eps); 
    return;
end
for i=1:n
    cur = X(ind(i));
    isMet = true;
    j=i+1;
    k=1;
    while (j<=n && isMet)
        if(cur==X(ind(j)))            
            X(ind(j))=X(ind(j))+k*eps;
            k=k+1;
        else
            isMet =false;
        end
        j=j+1;
    end
end
end
