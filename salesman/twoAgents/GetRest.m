function rest = GetRest(seq,agentsSeq)
rest = seq;
for i=1:size(agentsSeq,1)
    rest = setdiff(rest,agentsSeq(i,:));
end
%rest = rest';%row