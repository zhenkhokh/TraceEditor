% bounds
intcon = 1:lendist;
lb = zeros(lendist,1);
ub = ones(lendist,1);
% Optimize Using intlinprog
%opts = optimoptions('intlinprog','Display','off');
%[xopt,costopt,exitflag,output] = intlinprog(dist,intcon,[],[],Aeq,beq,lb,ub,opts);
xopt = linprog(dist,[],[],Aeq,beq,lb,ub);
xopt = round(xopt);
% % Visualize the Solution
 hold on
 segments = find(xopt); % Get indices of lines on optimal path
 lh = zeros(nStops,1); % Use to store handles to lines on plot
 lh = updateSalesmanPlot(lh,xopt,idxs,stopsLon,stopsLat);
 title('Solution with Subtours');
