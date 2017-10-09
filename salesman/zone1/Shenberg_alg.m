function [a b c d x y] = Shenberg_alg(bInd,X,Y)
% bInd - indexes that define monotonic part of boundary
% bInd[1:3] - boundary points(start and end) and direction of monotonic part
% bInd[4:end] - base sorting.
% if no previous part is existed than last one is taken
% c - second derivation if next out of bound points has same x with bound. 
% c(1)=0 same with start, c(n) - same with end.
% a = Y, b - first derivate, d - 6*coeff in 3-d member
% X,Y - not sorted all points 
ind = bInd(4:end);
n = length(ind);
a=zeros(1,n);b=zeros(1,n);c=zeros(1,n);
d=zeros(1,n);
a(1) = NaN;
b(1) = NaN;
d(1) = NaN;

aa = bInd(1);
ia = Get_i_ind(aa,ind);
bb = bInd(2);
ib = Get_i_ind(bb,ind);
i_1 = Get_ind(ia-1,ind);
i1 = Get_ind(ia+1,ind);
direction = bInd(3);
n = mod(ib-ia+1,n);
if(ib==ia)
    error('bad bInd');
end
x=zeros(1,n+1);y=zeros(1,n+1);
for i=1:n+1
    if(direction>0)
        x(i) = X(Get_Next(ia,(i-1),ind));
        y(i) = Y(Get_Next(ia,(i-1),ind));
    else
        x(i) = X(Get_Next(ib,-(i-1),ind));
        y(i) = Y(Get_Next(ib,-(i-1),ind));
    end
end

% if (direction<0)% use only positive direction
% 	tmp = ia;
% 	ia = ib;
% 	ib = tmp;
% 	tmp = aa;
% 	aa = bb;
% 	bb = tmp;
% 	i_1 = Get_ind(ia+1,ind);
% 	i1 = Get_ind(ia-1,ind);
% end
if (direction==0)
% 	if (Y(aa)<Y(bb))
% 		a(1) = Y(aa);
% 		b(1) = Y(bb);
%     elseif (Y(aa)>Y(bb))
% 		a(1) = Y(bb);
% 		b(1) = Y(aa);
%     end
    a = y(1:end-1);
    b(:) = NaN;
	if (Y(aa)==Y(bb))
		error('YA==YB, y-direction');
    end
	d = x(1:end-1);
	c(:) = NaN;
    x = x(1:end-1);
    y = y(1:end-1);
    return;
end

xA = X(aa);
yA = Y(aa);
xB = X(bb);
yB = Y(bb);
x_1 = X(i_1);
y_1 = Y(i_1);
x1 = X(i1);
y1 = Y(i1);

h1 = xA-x_1;
h2 = x1-xA;
if(h2==0)
	error('h2==0: not possible!');
end

if (length(x)>=3)
	if (h1~=0)
		%c(1) = (y_1*h2-yA*(h1+h2)+y1*h1)/(h1*h1*h2);% second derivate
        c(1)=0;
	else
		c(1) = 0;
    end
else
	c(1) = 0;
	c(2) = 0;
	a(2) = y1;
	b(2) = (y1-yA)/h2+h2*(2*c(2)-c(1))/6;
	d(2) = (c(2)-c(1))/h2;
    x = x(1:end-1);
    y = y(1:end-1);
    return;
end

if (length(x)>=3)
	alpha=zeros(1,n);beta=zeros(1,n);
	alpha(1)=0;
	beta(1) = c(2);
	%Thomas algorithm
	for i=3:n+1
		h1 = x(i-1)-x(i-2);
		h2 = x(i)-x(i-1);
		Fi_1 = 6*((y(i)-y(i-1))/h2-(y(i-1)-y(i-2))/h1);
		beta(i-1) = (Fi_1-h1*beta(i-2))/(h1*alpha(i-2)+2*(h1+h2));
		alpha(i-1) = -h2/(h1*alpha(i-2)+2*(h1+h2));
    end
	%Shenberg calc
	for i=2:n %n-1
		a(i) = y(i);
		h1 = x(i)-x(i-1);
		h2 = x(i+1)-x(i);
		Fi = 6*((y(i+1)-y(i))/h2-(y(i)-y(i-1))/h1);
        if (h2~=0)% always when i<n
%             c(i) = (Fi-h2*beta(i))/(h1*alpha(i)+2*(h1+h2));
            c(i)=0;
        else
            c(i)=0;
        end
		b(i) = (y(i)-y(i-1))/h1+h1*(2*c(i)+c(i-1))/6;
		d(i) = (c(i)-c(i-1))/h1;
    end
% 	i=n;
% 	a(i) = y(i);
% 	h1 = x(i)-x(i-1);
% 	h2 = x(i+1)-x(i);
% 	if (h2==0)
% 		c(i) = 0;
% 	else
% 		y_1 = y(i-1);
% 		yB = y(i);
% 		yA = y(i+1);
% 		%c(i) = (y_1*h2-yA*(h1+h2)+y1*h1)/(h1*h1*h2);
%         Fi = 6*((yA-yB)/h2-(yB-y_1)/h1);
%         c(i) = (Fi-h2*beta(i))/(h1*alpha(i)+2*(h1+h2));
%     end
% 	b(i) = (y(i)-y(i-1))/h1+h1*(2*c(i)+c(i-1))/6;
% 	d(i) = (c(i)-c(i-1))/h1;
end
x = x(1:end-1);
y = y(1:end-1);
end
