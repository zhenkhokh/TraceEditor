function [X, Y, ind] = InsertInnerPointsToYDirection(X,Y,ind)

x = X(ind);
y = Y(ind);
n = length(ind);
eps=10^-3;
for i=1:n
    if(i<n)
        j = i+1;
    else
        j = 1;
    end
    while(j<=n&&x(i)==x(j))
        inner = [eps*x(i)+(1-eps)*x(j) eps*y(i)+(1-eps)*y(j)];
        j=j+1;
    end
    if(j-1==i+1) % no inner point is existed
        n1 = length(ind);
        ind = [ind(1:i) n1+1 ind((i+1):n1)];
        X = [X inner(1)];
        Y = [Y inner(2)];
    end
end