function [ agentsSeq state ] = SimulatedAnnealing( cities, initialTemperature, endTemperature, initState)
    global aNum aSize positionMode tSteps positionRandom shaffleMode pushMode exchangeMode energy 
    [n, z] = size(cities); % �������� ������ ������� �������

    %state = randperm(n)'; % ����� ��������� ���������, ��� ��������� ������������ �������
   
    state = initState;
    agentsSeq = zeros(aNum,aSize);
    inits = zeros(1,aNum);
    inits(1) = 1;
    for i=2:aNum
        inits(i) = GetInit(cities,positionRandom,inits);
    end
    if (aNum>1)        
        agentsSeq(1,1) = 1;
        secondStartPos = GetInit(cities,positionMode);
        agentsSeq(2,1) = secondStartPos;
        tmp = setdiff(state,inits);
        %agentsSeq(1,2:end) = tmp(1:aSize-1);
        %agentsSeq(2,2:end) = tmp(aSize:(2*aSize-2));
        for i=1:aNum
            agentsSeq(i,2:end) = tmp(((i-1)*(aSize-1)+1):(i*aSize-i));
            agentsSeq(i,1) = inits(i);
        end
    elseif (aNum==1)
        agentsSeq = state';    
    end        

    currentEnergy = CalculateEnergy(state, cities); % ��������� ������� ��� ������� ���������
    T = initialTemperature;
    initEnergy = currentEnergy;
    initState = state;
    freq = 101;
    
    for k = 1:tSteps         
        %mode = mod(k,2)+2;
       mode = shaffleMode;
       if (mod(k,freq)==0)
           mode = exchangeMode;
       end
       if (mod(k,2*freq)==0)
           mode = pushMode;
       end

        %mode = shaffleMode;
        stateCandidate = GenerateStateCandidate(GetRest(state,agentsSeq),mode,agentsSeq,cities); % �������� ���������-��������
        %stateCandidate = GenerateStateCandidate1(state);
        candidateEnergy = CalculateEnergy(stateCandidate, cities); % ��������� ��� �������
%         if (mod(k,freq)==0)
%             if (initEnergy>currentEnergy)
%             tmp  = currentEnergy;
%             currentEnergy = initEnergy;
%                 initEnergy = tmp;
%                 initState = state;
%             else
%                 state = initState;
%             end
%         end
        if(candidateEnergy < currentEnergy) % ���� �������� �������� ������� ��������
            currentEnergy = candidateEnergy; % �� �� ��������� � ������� ���������
            state = stateCandidate;
        else
            p = GetTransitionProbability(candidateEnergy-currentEnergy, T); % �����, ������� �����������
            if (MakeTransit(p)) % � �������, ������������ �� �������
                currentEnergy = candidateEnergy;
                state = stateCandidate;
            end
        end;
energy(k) = currentEnergy;
        %agentsSeq = state';
        agentsSeq = reshape(state(1:(aSize*aNum)),aSize,aNum)';
        T = DecreaseTemperature(initialTemperature, k) ; % ��������� �����������
        
        if(T <= endTemperature) % ������� ������
            break;
        end;
    end

end

