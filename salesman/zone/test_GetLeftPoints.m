function test_GetLeftPoints()
xleft = [1 2 3 4 5];
xrigth = [5 5 4 3 2 1];
xx = [xleft xrigth 1];
xM = zeros(3,10);
xM(1,1:5)=xleft;
xM(1,6:end)=NaN;
xM(2,1)=xrigth(1);
xM(2,2:end)=NaN;
xM(3,1:5)=[xrigth(2:end)];
xM(3,6:end)=NaN;
% xM(1,6) = xM(2,1);
% xM(2,2) = xM(3,1);
% xM(3,6) = xM(1,1);
xt = [-1 0:.5:11];
for x=xt
    [ia ib ja jb] = Get_LeftPoints_(x,xM,min(xx));
    try,[ia ib ja jb] = Get_LeftPoints_(x,xM,min(xx));catch disp(['ia=' num2str(ia) ' ib=' num2str(ib)]), end
    if (~isempty(find(x<=xx, 1)))
        disp(['left x=' num2str(x) ' ia ib ja jb ' num2str([ia ib ja jb])])
    end
    if (~isempty(find(xx<x, 1)))
        disp(['rigth x=' num2str(x) ' ia ib ja jb ' num2str([ia ib ja jb])])
    end
end