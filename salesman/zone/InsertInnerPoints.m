function [X, Y, ind] = InsertInnerPoints(X,Y,ind)

x = X(ind);
y = Y(ind);
n = length(ind);
of = 1;
for eps=[10^-3 1-10^-3]
offset=0;
% eps=1-10^-3;
    for i=1:n
        if(i<n)
            j = i+1;
        else
            j = 1;
        end
        inner = [eps*x(i)+(1-eps)*x(j) eps*y(i)+(1-eps)*y(j)];
        n1 = length(X);
        ind = [ind(1:(offset*of+i)) n1+1 ind((i+offset*of+1):end)];
        X = [X inner(1)];
        Y = [Y inner(2)];
        offset = offset+1;
    end
    of = of+1;
end
end