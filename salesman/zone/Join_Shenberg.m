function [aM, bM, cM, dM, x, y] = Join_Shenberg(X,Y)
%aM[n,n],...;

startIndex=1;
boundInd = Get_Monotonic_Part(X,Y,startIndex);
ind = boundInd(4:end);
m=0;
len = length(X);
for i=1:len
    tmp=ind(1);
    ind(1:end-1)=ind(2:end);
    ind(end)=tmp;
    mc = Get_PartsNumber(X,Y,ind);
    if(m<mc)
        disp(['  ' num2str(ind(1))])
        startIndex = ind(1);
        m = mc;
    end
end

% mm = inf;
% startCicleCnt=0;
% % TODO undestand startIndex cicle
% while (m~=mm && startCicleCnt<len)%! m>mm
k=1;
whileTrue = true;
boundInd = Get_Monotonic_Part(X,Y,startIndex);
lBound = boundInd(1);

prev = Get_ind(Get_i_ind(startIndex,ind)-1,ind);
aM = zeros(m,len);
bM = zeros(m,len);
cM = zeros(m,len);
dM = zeros(m,len);
x=zeros(m,len);
y=zeros(m,len);
firstPartIsPreLast = false;
while(whileTrue)
	boundInd = Get_Monotonic_Part(X,Y,startIndex);
    if (k==2)
        len2 = Get_i_ind(boundInd(2),ind)-Get_i_ind(boundInd(1),ind);
        firstPartIsPreLast = length(ind)-len1-len2+2==0;
    elseif(k==1)
        len1 = Get_i_ind(boundInd(2),ind)-Get_i_ind(boundInd(1),ind);
    end
    
    firstPartIsNotSmall = true;
    if(prev~=boundInd(1)&&k>1)%!
        direction = sign(X(startIndex)-X(prev));
        if(direction~=boundInd(3)||(direction==0&&boundInd(3)==0))
            boundInd(1:3) = [prev startIndex direction];                
            %firstPartIsNotSmall = false;
        end
    end

    prev = boundInd(2);
    startIndex = Get_ind(Get_i_ind(prev,ind)+1,ind);
	[a, b, c, d, xx, yy] = Shenberg_alg(boundInd,X,Y);
    n = length(xx);%n1=Get_i_ind(boundInd(1),ind);n2=Get_i_ind(boundInd(2),ind); for s=n1:n2, X(ind(s)),end, n1:n2, ind(n1:n2)
	aM(k,1:n) = a(1:n);
    aM(k,n+1:len) = NaN;
	bM(k,1:n) = b(1:n);
    bM(k,n+1:len) = NaN;
	cM(k,1:n) = c(1:n);
    cM(k,n+1:len) = NaN;
	dM(k,1:n) = d(1:n);
    dM(k,n+1:len) = NaN;
    x(k,1:n) = xx;
    x(k,n+1:len) = NaN;
    y(k,1:n) = yy;
    y(k,n+1:len) = NaN;

    if ((k>1 && firstPartIsNotSmall) || firstPartIsPreLast)
        if (boundInd(1)==lBound||boundInd(2)==lBound)
            break;
        end
    end
	k=k+1;
end
mm = k;
[~,n]=size(x);
whereFirstPartRepeated=0;
n1=0;
for i=1:k
    cnt=1;
    ni=cnt;
    while(~isnan(x(i,ni))&& ni<n)
        cnt=cnt+(x(i,ni)==x(1,ni));
        ni = ni+1;
    end
    if (i==1)
        n1=ni;
    end
    if(i>2 && cnt==ni && ni==n1)
        whereFirstPartRepeated = i;
    end
end

% reduce size
if (whereFirstPartRepeated>0)
    k = whereFirstPartRepeated;
    aM(k,:)=[];bM(k,:)=[];cM(k,:)=[];dM(k,:)=[];x(k,:)=[];y(k,:)=[];
    [mm,~]=size(x);% actual size
end
% startIndex=Get_Next(Get_i_ind(startIndex,ind),1,ind);
% startCicleCnt=startCicleCnt+1;
% end
end

