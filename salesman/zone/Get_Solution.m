function result = Get_Solution(Xt,Yt,X,Y)

% X = X+Get_x_offset(X);
% Xt = Xt+Get_x_offset(X);
m = length(Xt);
[aM, bM, cM, dM, xM, yM] = Join_Shenberg(X,Y);
result = zeros(1,m);
for i=1:m
	result(i) = Define_point(Xt(i),Yt(i),aM,bM,cM,dM,xM,yM,min(X));
end
end