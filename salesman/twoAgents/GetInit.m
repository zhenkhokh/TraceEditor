function ind = GetInit(cities,mode,inits)
global positionClose positionFar positionRandom
n = size(cities,1);
distFromFirst = zeros(1,n-1);
for i = 2:n
    distFromFirst(i-1) = Metric(cities(1,:),cities(i,:));
end
if(mode==positionClose)
    [~,ind] = min(distFromFirst);
end
if(mode==positionFar)
    [~,ind] = max(distFromFirst);
end
if(mode==positionRandom)
    ind = randi([2 n],1);
    while(~isempty(inits(inits==ind)))
        ind = randi([2 n],1);% keep first
    end    
end
end