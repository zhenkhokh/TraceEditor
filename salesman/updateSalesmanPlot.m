function lh = updateSalesmanPlot(lh,xopt,idxs,stopsLat,stopsLon)
% tsp_intlinprog例子的绘图函数
 
if ( lh ~= zeros(size(lh)) ) % 通过lh是否全零来判断是否第一次进入
    set(lh,'Visible','off'); % 移除之前的线
end
 
% 求解结果中的旅行
segments = find(xopt); 
 
% 形成绘制数据
Lat = zeros(3*length(segments),1);
Lon = zeros(3*length(segments),1);
for ii = 1:length(segments)
    start = idxs(segments(ii),1);
    stop = idxs(segments(ii),2);
    
    % 数据之间用NaN分割，这样可以绘制分离的线段，不然需要一个一个绘制线段，产生很多句柄
    Lat(3*ii-2:3*ii) = [stopsLat(start); stopsLat(stop); NaN];
    Lon(3*ii-2:3*ii) = [stopsLon(start); stopsLon(stop); NaN];
end
 
lh = plot(Lat,Lon,'k:','LineWidth',2);
set(lh,'Visible','on');
drawnow;
