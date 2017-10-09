function pos = Get_Next(i,offset,ind)
i = i+offset;
n = length(ind);
pos = ind(1+mod(i-1,n));