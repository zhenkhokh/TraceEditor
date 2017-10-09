global tSteps shaffleMode pushMode exchangeMode positionClose positionFar positionRandom positionMode aNum aSize energy
tSteps = 100000;
pushMode = 1;
shaffleMode = 2;
exchangeMode = 3;

positionClose = 1;
positionFar = 2;
positionRandom = 3;
positionMode = positionFar;

energy = ones(1,tSteps);
try n; catch, n=50; end
try data; catch, data = rand(2,n)*10; end
aNum =1; aSize = n;
%[~, state] = SimulatedAnnealing(data', 10,10^-5,randperm(n)');
aNum = 3;
aSize = 12;
tSteps = 1;
[agentsSeq state] = SimulatedAnnealing(data', 10,10^-5,state);
wh_plot
plot(data(1,:),data(2,:),'*-')
title(['random voyage, S=' num2str(CalculateEnergy((1:n)',data'))])
if (size(agentsSeq,1)>=2)
    wh_plot
    plot(data(1,:),data(2,:),['.' 'b']...
    ,data(1,agentsSeq(1,:)),data(2,agentsSeq(1,:)),['*-' 'g']...
    ,data(1,agentsSeq(2,:)),data(2,agentsSeq(2,:)),['*-' 'b']...
    ,data(1,agentsSeq(1,1)),data(2,agentsSeq(1,1)),['p' 'r']...
    ,data(1,agentsSeq(2,1)),data(2,agentsSeq(2,1)),['p' 'r'])
    s1 = CalculateEnergy(agentsSeq(1,:),data');
    s2 = CalculateEnergy(agentsSeq(2,:),data');
    legend('vacant','1','2')
    title(sprintf(['two agents voyage, S_1=' num2str(s1) ...
        ' S_2=' num2str(s2) ...
        ' S=' num2str(s1+s2)]))
end
if (size(agentsSeq,1)==3)
    wh_plot
    plot(data(1,:),data(2,:),['.' 'b']...
    ,data(1,agentsSeq(1,:)),data(2,agentsSeq(1,:)),['*-' 'g']...
    ,data(1,agentsSeq(2,:)),data(2,agentsSeq(2,:)),['*-' 'b']...
    ,data(1,agentsSeq(3,:)),data(2,agentsSeq(3,:)),['*-' 'c']...
    ,data(1,agentsSeq(1,1)),data(2,agentsSeq(1,1)),['p' 'r']...
    ,data(1,agentsSeq(2,1)),data(2,agentsSeq(2,1)),['p' 'r']...
    ,data(1,agentsSeq(3,1)),data(2,agentsSeq(3,1)),['p' 'r'])
    s1 = CalculateEnergy(agentsSeq(1,:),data');
    s2 = CalculateEnergy(agentsSeq(2,:),data');
    s3 = CalculateEnergy(agentsSeq(3,:),data');
    legend('vacant','1','2','3')
    title(sprintf(['three agents voyage \n S_1=' num2str(s1) ...
        ' S_2=' num2str(s2) ...
        ' S_3=' num2str(s3) ...
        ' S=' num2str(s1+s2+s3)]))
end
wh_plot
plot(data(1,state),data(2,state),['*-' 'b'])
title('one agent voyage')
wh_plot
plot(energy)
title('energy')
