function X = prepareSqure(X,Y,eps)
%change top rigth corner
i=1;
if (X(1)<=X(2) && X(3)<=X(2) && X(4)<=X(2) && Y(1)<=Y(2) && Y(3)<=Y(2) && Y(4)<=Y(2))
    i=2;
end
if (X(1)<=X(3) && X(2)<=X(3) && X(4)<=X(3) && Y(1)<=Y(3) && Y(2)<=Y(3) && Y(4)<=Y(3))
    i=3;
end
if (X(1)<=X(4) && X(2)<=X(4) && X(3)<=X(4) && Y(1)<=Y(4) && Y(2)<=Y(4) && Y(3)<=Y(4))
    i=4;
end
X(i) = X(i)+eps;