function [i, j]  = Get_NextB(i,offset,last,m,n)
if (offset>n-1 && offset<0)
	error('offset must be >0 and <=n-1');
end
j=1+offset;
% if(mod(i,2)==1)
% 	j = 1+offset;
% else
% 	j = last-offset;%
% end
% if (j>last)
% 	%j = j-last+1;
%     j=NaN;
% 	i=1+mod(i,m);
% end
% if(j<=0)
% 	%j=offset-last+1;
%     j=NaN;
% 	i=1+mod(i,m);
% end