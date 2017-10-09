opt=sum(abs(x(1:end-1)-x(2:end))+abs(y(1:end-1)-y(2:end)));
main = zeros(2,length(x));
others = zeros(1,length(x));
main(1,:) = x;
main(2,:) = y;
whileTrue=true;
ind = 1:length(x)
while(whileTrue)
    whileTrue = false;
    for i=1:length(x)
        for j=2:length(x)
            tmp = main(:,j);
            main(:,j) = main(:,i);
            main(:,i) = tmp;
            tmp = ind(j);ind(j)=ind(i);ind(i)=tmp;
            cur=sum(((main(1,1:end-1)-main(1,2:end)).^2+(main(2,1:end-1)-main(2,2:end)).^2).^.5);
            %cur=sum(abs(main(1,1:end-1)-main(1,2:end))+abs(main(2,1:end-1)-main(2,2:end)));%+...
                %sum(abs(others(1,1:end-1)-others(1,2:end))+abs(others(2,1:end-1)-others(2,2:end)));
             if(cur<opt)
                 opt = cur;
                 disp(['opt=' num2str(opt)])
                 whileTrue = true;
             else
                tmp = main(:,j);
                main(:,j) = main(:,i);
                main(:,i) = tmp;
                tmp = ind(j);ind(j)=ind(i);ind(i)=tmp;
             end
        end
    end
end        
   