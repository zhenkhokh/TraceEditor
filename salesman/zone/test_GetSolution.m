n=10;
eps=10^-9;% check it whith other sign
X = [9.9631    6.3541    7.7109    2.5855    1.9888    2.3863    5.1919    6.1869    0.9988    4.0624];
Y = [9.7119    6.5055    7.8291    0.7998    4.8056    0.6652    4.3464    5.4613    8.5728    3.9221];
[~,ind] = sort(X);
X0=X(ind);Y0=Y(ind);
ind = Get_Monotonic_Part(X0,Y0,1);
ind = ind(4:end);
[X0, Y0] = InsertInnerPoints(X0,Y0,ind);
% triangle with 3 inner points
X1 = [-1 0 1 1/2 0 -1/2];
Y1 = [0 0 0 3^.5/2 3^.5 3^.5/2];
% triangle with one inner point
X2 = [ -1 1 0 ];
Y2 = [ 0 0 3^.5];
ind = Get_Monotonic_Part(X2,Y2,1);
ind = ind(4:end);
[X2, Y2] = InsertInnerPoints(X2,Y2,ind);
% triangle with y-direction
X21 = [0 -1 1 0 0];
Y21 = [0 0 0 3^.5 1/3^.5];
X21=removeYDirection(X21,Y21,eps);
% home 
X3 = [0 1 1 0 -1 -1 ];
Y3 = [-.25 0 .5 1 0 -1];
ind = Get_Monotonic_Part(X3,Y3,1);
ind = ind(4:end);
[X3, Y3] = InsertInnerPoints(X3,Y3,ind);
X3=removeYDirection(X3,Y3,eps);
% home with 3 inner points
X4=[0 1 1 1 0 -.5 -1 -1 -1];
Y4=[-.25 0 .25 .5 1 .5 0 -.5 -1];
% ind = Get_Monotonic_Part(X4,Y4,1);
% ind = ind(4:end);
% [X4, Y4] = InsertInnerPoints(X4,Y4,ind);
X4=removeYDirection(X4,Y4,eps);
% circle
X5=[-1:.05:1];
Y5 = zeros(size(X5));
Y5(1:2:end) = (1-X5(1:2:end).^2).^.5;
Y5(2:2:end) = -(1-X5(2:2:end).^2).^.5;
% parallelogram
a=.3;
X6=[-1 -1+a 1 1+a];
Y6=[-1 1 -1 1];
%X6 = removeYDirection(X6,Y6,eps);
% squre
X7=[-1 -1 1 1];
Y7=[-1 1 -1 1];
X7 = removeYDirection(X7,Y7,eps);
% squre with 2 inner points
X8=[-1 -1 1 1 -1 1];
Y8=[-1 1 -1 1 0 .5];
X8 = removeYDirection(X8,Y8,eps);% cant be commented

m=10;
for j=0:m
    if(j==0)
        X=X0;Y=Y0;
    elseif(j==1)
        X=X1;Y=Y1;
    elseif(j==2)
        X=X2;Y=Y2;
    elseif(j==3)
        X=X21;Y=Y21;        
    elseif(j==4)
        X=X3;Y=Y3;
    elseif(j==5)
        X=X4;Y=Y4;        
    elseif(j==6)
        X=X5;Y=Y5;
    elseif(j==7)
        X=X6;Y=Y6;
    elseif(j==8)
        X=X7;Y=Y7;
    elseif(j==9)
        X=X8;Y=Y8;
    else
        X=10*rand(1,n);
        Y=10*rand(1,n);
        [~,ind] = sort(X);% comment
        X=X(ind);Y=Y(ind);
    end
    
    xMin=min(X)-0.2*(abs(min(X))+abs(max(X)));
    xMax=max(X)+.1*(abs(min(X))+abs(max(X)));xStep=(xMax-xMin)/1000;
    xt=xMin:xStep:xMax;
    yMin=min(Y)-.1*(abs(min(Y))+abs(max(Y)));
    yMax=max(Y)+.1*(abs(min(Y))+abs(max(Y)));
    yt=(yMax-yMin)*rand(1,length(xt))+yMin;
    result = Get_Solution(xt,yt,X,Y);
    ind0 = result==0;
    ind1 = result==1;
    wh_plot

    ind = Get_Monotonic_Part(X,Y,1);
    ind = ind(4:end);

    plot(xt(ind0),yt(ind0),['*' 'g'],xt(ind1),yt(ind1),['p' 'r'],X(ind),Y(ind),'.-')
end
% [m n] = size(xM);
% xsM = zeros(m,length(xs));
% ysM = zeros(m,length(xs));
% wh_plot
% hold on
% for i=1:m
% 	xmax = max(xM(i,:));
% 	xmin = min(xM(i,:));
% 	[~,ind1] = find(xmin<xs);
%     [~,ind2] = find(xs<=xmax);
%     ind = intersect(ind1,ind2);
% 	ns = length(ind);
% 	xsM(i,1:ns)=xs(ind);
% 	xsM(i,ns+1:end)=NaN;
% 	for ii=1:ns
%         x = xs(ind(ii));
% 		[a b c d xB pos] = Get_abcd_xB(xs(ind(ii)),i,xM,aM,bM,cM,dM);
%         if (pos==0)
%             if (~isnan(a))
%                 ysM(i,ii) = d/6*(x-xB)*(x-xB)*(x-xB)+c/2*(x-xB)*(x-xB)+b*(x-xB)+a;
%             else
%                 ysM(i,ii) = (max(yM(i,:))+min(yM(i,:)))/2;
%             end
%         else
%             ysM(i,ii) = NaN;
%         end
%     end
% 	plot(xs(ind),ysM(i,1:ns),'.-')
% end