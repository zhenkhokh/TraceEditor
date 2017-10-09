n=10;
X=10*rand(1,n);
Y=10*rand(1,n);
iNd =1:n;
for i=1:floor(length(X)/2)
    g = i+1+round((length(X)-i)*rand()); 
    if (g>length(X))
        g=i+1;
    end
    tmp = iNd(g);
    iNd(g) = iNd(i);
    iNd(i) = tmp;
end
X = X(iNd);
Y = Y(iNd);
% X = [0 1 1 0 -.5 -1 -1 -1 -1 -1];
% Y = [-.25 0 .5 1 .5 0 -.5 -1 -1 -1];
[in X Y] = Connector_define(X,Y);
wh_plot
title('min perimeter')
plot(X(in),Y(in))
% forget about it!
% in1 = Connector_define_int(X,Y);
% wh_plot
% title('max area')
% plot(X(in1),Y(in1))
% 
% in==in1
