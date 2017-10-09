function seq = GenerateStateCandidate(rest,mode,agentsSeq,data)
global shaffleMode pushMode exchangeMode
    n = size(agentsSeq,2);    
    if (mode==shaffleMode)
        %for ii=1:size(agentsSeq,1)
        ii=1;
        %seq = agentsSeq';
            i = randi([2 n],1); % keep first
            j = randi([2 n], 1);% keep first
            if(i > j)
                %seq(j:i) = flipud(seq(j:i));                
                agentsSeq(ii,j:i) = fliplr(agentsSeq(ii,j:i));
            else
                %seq(i:j) = flipud(seq(i:j));
                agentsSeq(ii,i:j) = fliplr(agentsSeq(ii,i:j));
            end
        %end
    end
    if (mode==pushMode && ~isempty(rest))
        m = length(rest);
        ir = randi(m,1);
        pushed = rest(ir);
        %j = randi([2 n],1);% keep first
        ii = 1;
%         tmp = inf;
%         pushedElement = data(pushed,:);
%         for j=2:n % find closest and keep first
%             dist = Metric(data(agentsSeq(ii,j)),pushedElement);
%             if (dist<tmp && dist>0)
%                 jj=j;
%                 tmp = dist;
%             end        
%         end
        rest(ir) = agentsSeq(ii,n);
        agentsSeq(ii,n) = pushed;
    end
    if (mode==exchangeMode && size(agentsSeq,1)>=2)
        i = randi([2 n],1); % keep first
        j = randi([2 n], 1);% keep first
        tmp = agentsSeq(1,i);
        agentsSeq(1,i) = agentsSeq(2,j);
        agentsSeq(2,j) = tmp;
    end

    agentsSeq = Shaffle(agentsSeq);   
    seq=zeros(numel(agentsSeq)+length(rest),1);
    activeLen = numel(agentsSeq);
    seq(1:activeLen) = reshape(agentsSeq',activeLen,1);
    if (length(rest)>=1)
        seq((activeLen+1):end) = rest;
    end
    
end
