function boundInd = Get_Monotonic_Part(x,y,anyIndex)

if (length(x)~=length(y))
	error('length(x)~=length(y)');
end
ind = Connector_define(x,y);
n = length(ind);
if(n~=length(x))
	error('length(ind)~=length(x)');
end
if (anyIndex<1||anyIndex>n)
   	error('anyIndex is out of size');
end
i = Get_i_ind(anyIndex,ind);
lowIndex = Get_ind(i-1,ind);
upIndex = Get_ind(i+1,ind);
%define local sorting
endIndex = -1;
startIndex = -1;
if ( x(lowIndex)<x(anyIndex) && x(anyIndex)<x(upIndex) )    % 	1. x(-1)<x(0)<x(1). Inner positive
	xDirection = 1;
elseif(x(lowIndex)>=x(anyIndex) && x(anyIndex)<x(upIndex)) % 	2. x(0)<=x(-1),x(1)[~=x(0)]. L-bound positive
	xDirection = 1;
	endIndex = anyIndex;
elseif(x(lowIndex)<x(anyIndex) && x(anyIndex)>=x(upIndex)) % 	3. x(-1)[~=],x(1)<=x(0). R-bound positive
	xDirection = 1;    
	startIndex = anyIndex;
elseif(x(lowIndex)==x(anyIndex) && x(anyIndex)==x(upIndex))%	4. Inner Y direction.
	xDirection = 0;    
elseif(x(lowIndex)>x(anyIndex) && x(anyIndex)>x(upIndex))  %	5. x(1)<x(0)<x(-1). Inner negative
	xDirection = -1;
elseif(x(lowIndex)==x(anyIndex) && x(anyIndex)>x(upIndex)) % 	6. Always case x(-1)=x(0)<x(1). L-bound negative
	xDirection = -1;
	startIndex = anyIndex;
elseif(x(lowIndex)>x(anyIndex) && x(anyIndex)==x(upIndex)) % 	7. Always case x(-1)<x(0)=x(1). R-bound negative
	xDirection = -1;
	endIndex = anyIndex;
end
   

while(endIndex==-1 || startIndex==-1 && xDirection~=0)
	next = anyIndex;
	willPartGet = true;
	offset=0;
	while(willPartGet)
		willPartGet = false;
		cur = next;

		if(endIndex==-1) 
			offset = offset-xDirection;
			next = Get_Next(i,offset,ind);
%             next = next-xDirection;
			%%
% 			if (xDirection==1) % L-positive
% 				next = Get_ind(next-1,ind);
% 			else if (xDirection==-1)% R-negative
% 				next = Get_ind(next+1,ind);
% 			endIf
			%%
			if (x(next)<x(cur)) % L-positive,R-negative, I-positive/negative
				willPartGet = true;
			else
				endIndex = cur;
            end
        elseif (startIndex==-1)
			offset = offset+xDirection;
			next = Get_Next(i,offset,ind);
%             next = next+xDirection;
			if (x(cur)<x(next)) % R-positive,L-negative
				willPartGet = true;
			else
				startIndex = cur;
            end
        end
    end
end

if (xDirection==0)
	while(endIndex==-1 || startIndex==-1) %
		if(startIndex==-1)% go to startIndex
			direction = 1;
        elseif(endIndex==-1) % go to endIndex
			direction = -1;
        end
		next = anyIndex;
		willPartGet = true;
		offset = 0;
		while(willPartGet)
			willPartGet = false;
			cur = next;
			offset = offset+direction;
			next = Get_Next(i,offset,ind);
			if(x(cur)~=x(next))
				if (direction==1)
					startIndex = cur;
				else % -1
					endIndex = cur;
                end
			else
				willPartGet = true;
                if(offset==n*direction)% not closed curve!
                    startIndex = 1;
                    endIndex = n;
                    willPartGet = false;
                end
            end
        end
    end
 end

boundInd = zeros(1,3+n);
boundInd(1) = startIndex;
boundInd(2) = endIndex;
boundInd(3) = xDirection;
if ((x(endIndex)-x(startIndex))*xDirection<0&&xDirection~=0)
	boundInd(2) = startIndex;
    boundInd(1) = endIndex;
end
%!
if (xDirection==0 && Get_i_ind(startIndex,ind)>Get_i_ind(endIndex,ind))
	boundInd(2) = startIndex;
    boundInd(1) = endIndex;
end
if (x(endIndex)~=x(startIndex)&&xDirection==0)
   	error('bad algorithm,Direction==0');
end
boundInd(4:end) = ind;
end
