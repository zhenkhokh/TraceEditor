try n; catch, n=10; end
data = rand(2,n)*10;
state = SimulatedAnnealing(data', 10,10^-5);
wh_plot
plot(data(1,:),data(2,:),'*-')
title('random voyage')
wh_plot
plot(data(1,state),data(2,state),'*-')
title('optimized voyage')
