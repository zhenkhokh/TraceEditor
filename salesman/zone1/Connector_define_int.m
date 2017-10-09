function ind = Connector_define_int(x,y)

ind = 1:length(x);
opt=sum([(y(1:end-1)+y(2:end)).*(x(2:end)-x(1:end-1)) (y(1)+y(end))*(x(1)-x(end))]);
main = zeros(2,length(x));
main(1,:) = x;
main(2,:) = y;
whileTrue=true;
while(whileTrue)
    whileTrue = false;
    for i=1:length(x)
        for j=2:length(x)
            main(:,i:j)=fliplr(main(:,i:j));
            ind(i:j) = fliplr(ind(i:j));
            cur=sum([(main(2,1:end-1)+main(2,2:end)).*(main(1,2:end)-main(1,1:end-1)) ...
                (main(2,1)+main(2,end))*(main(1,1)-main(1,end))]);
             if(cur>opt)
                 opt = cur;
                 disp(['opt=' num2str(opt)])
                 whileTrue = true;
             else
                main(:,i:j)=fliplr(main(:,i:j));
                ind(i:j) = fliplr(ind(i:j));
             end
        end
    end
    for i=1:length(x)
        for j=2:length(x)
            tmp = main(:,j);
            main(:,j) = main(:,i);
            main(:,i) = tmp;
            tmp = ind(j);ind(j)=ind(i);ind(i)=tmp;
            cur=sum([(main(2,1:end-1)+main(2,2:end)).*(main(1,2:end)-main(1,1:end-1)) ...
                (main(2,1)+main(2,end))*(main(1,1)-main(1,end))]);
            if(cur>opt)
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