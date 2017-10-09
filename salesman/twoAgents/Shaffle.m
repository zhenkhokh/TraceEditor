function A = Shaffle(A)

tmp = A(1,:);
n = size(A,1);
for i=2:n
    A(i-1,:)=A(i,:);
end
A(n,:)=tmp;