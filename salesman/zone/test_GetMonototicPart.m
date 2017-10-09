function test_GetMonototicPart()

X1 = [-1 0 1 1/2 0 -1/2];
Y1 = [0 0 0 3^.5/2 3^.5 3^.5/2];
direction1 = [1 1 1 -1 -1 -1];% positive is stronger than negative, A is always choos positive
X2 = [0 1 1 1 0 -.5 -1 -1 -1 ];
Y2 = [-.25 0 .25 .5 1 .5 0 -.5 -1];
direction2 = [1 1 0 -1 -1 -1 -1 0 1]; % negative is stronger than zero, A and B is always not belong to zero
n=length(X1);
bInd = zeros(n,3+n);
for i=1:n
    bInd(i,:) = Get_Monotonic_Part(X1,Y1,i);    
end
sum(bInd(:,3)'==direction1)==length(X1)

n=length(X2);
bInd = zeros(n,3+n);
for i=1:n
    bInd(i,:) = Get_Monotonic_Part(X2,Y2,i);
end
sum(bInd(:,3)'==direction2)==length(X2)