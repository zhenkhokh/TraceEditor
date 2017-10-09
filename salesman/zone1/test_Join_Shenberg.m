n=10;
% X=(10*rand(1,n));
% Y=(10*rand(1,n));
% X = [0 1 1 1 0 -.5 -1 -1 -1 ];
% Y = [-.25 0 .25 .5 1 .5 0 -.5 -1];
[aM bM cM dM xM yM]=Join_Shenberg(X,Y);

xs=0:.01:10;
[m n] = size(xM);
xsM = zeros(m,length(xs));
ysM = zeros(m,length(xs));
wh_plot
hold on
for i=1:m
	xmax = max(xM(i,:));
	xmin = min(xM(i,:));
	[~,ind1] = find(xmin<xs);
    [~,ind2] = find(xs<=xmax);
    ind = intersect(ind1,ind2);
	ns = length(ind);
	xsM(i,1:ns)=xs(ind);
	xsM(i,ns+1:end)=NaN;
	for ii=1:ns
        x = xs(ind(ii));
		[a b c d xB pos] = Get_abcd_xB(xs(ind(ii)),i,xM,aM,bM,cM,dM);
        if (pos==0)
            if (~isnan(a))
                ysM(i,ii) = d/6*(x-xB)*(x-xB)*(x-xB)+c/2*(x-xB)*(x-xB)+b*(x-xB)+a;
            else
                ysM(i,ii) = (max(yM(i,:))+min(yM(i,:)))/2;
            end
        else
            ysM(i,ii) = NaN;
        end
    end
	plot(xs(ind),ysM(i,1:ns),'.-')
end
ind = Get_Monotonic_Part(X,Y,1);
ind = ind(4:end);
plot(X(ind),Y(ind),['p-' 'r'])