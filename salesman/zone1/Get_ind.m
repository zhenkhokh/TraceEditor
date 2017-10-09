function i = Get_ind(i,ind)
 n = length(ind);
 i = ind(1+mod(i-1,n));
