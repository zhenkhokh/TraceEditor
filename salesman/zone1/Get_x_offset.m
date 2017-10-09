function offset = Get_x_offset(X)
% X'=X+offset, X'>0
offset = 2*min(X);
if (offset<0)
	offset = -offset;
elseif (offset==0)
	offset = max(X);
elseif (offset>0)
	offset=0;
end
end