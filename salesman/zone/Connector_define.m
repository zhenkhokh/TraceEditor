function [ind x y] = Connector_define(x,y)
normStreet=true;

% if (normStreet)
%     opt=sum(abs(x(1:end-1)-x(2:end))+abs(y(1:end-1)-y(2:end)))+...
%         abs(x(1)-x(end))+abs(x(1)-x(end));
% else
%     opt=sum(((x(1:end-1)-x(2:end)).^2+(x(1:end-1)-x(2:end)).^2).^.5)+...
%      ((x(1)-x(end))^2+(x(1)-x(end))^2)^.5;
% end
opt=inf;
main = zeros(2,length(x));
main(1,:) = x;
main(2,:) = y;
% x = x(iNd);
% y = y(iNd);
ind =1:length(x);
whileTrue=true;
while(whileTrue)
    whileTrue = false;
    for i=1:length(x)
        for j=2:length(x)
            main(:,i:j)=fliplr(main(:,i:j));
            ind(i:j) = fliplr(ind(i:j));
            if (normStreet)
                cur=sum(abs(main(1,1:end-1)-main(1,2:end))+abs(main(2,1:end-1)-main(2,2:end)))+...
                abs(main(1,1)-main(1,end))+abs(main(2,1)-main(2,end));
            else
                cur=sum(((main(1,1:end-1)-main(1,2:end)).^2+(main(2,1:end-1)-main(2,2:end)).^2).^.5)+...
                ((main(1,1)-main(1,end))^2+(main(2,1)-main(2,end))^2)^.5;
            end
             if(cur<opt)
                 opt = cur;
%                  disp(['opt=' num2str(opt)])
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
            if (normStreet)
                cur=sum(abs(main(1,1:end-1)-main(1,2:end))+abs(main(2,1:end-1)-main(2,2:end)))+...
                abs(main(1,1)-main(1,end))+abs(main(2,1)-main(2,end));
            else
                cur=sum(((main(1,1:end-1)-main(1,2:end)).^2+(main(2,1:end-1)-main(2,2:end)).^2).^.5)+...
                ((main(1,1)-main(1,end))^2+(main(2,1)-main(2,end))^2)^.5;
            end
             if(cur<opt)
                 opt = cur;
%                  disp([' opt=' num2str(opt)])
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


% dual part

whileTrue=true;
while(whileTrue)
    whileTrue = false;
    for i=1:length(x)
        for j=2:length(x)
            tmp = main(:,j);
            main(:,j) = main(:,i);
            main(:,i) = tmp;
            tmp = ind(j);ind(j)=ind(i);ind(i)=tmp;
            if (normStreet)
                cur=sum(abs(main(1,1:end-1)-main(1,2:end))+abs(main(2,1:end-1)-main(2,2:end)))+...
                abs(main(1,1)-main(1,end))+abs(main(2,1)-main(2,end));
            else
                cur=sum(((main(1,1:end-1)-main(1,2:end)).^2+(main(2,1:end-1)-main(2,2:end)).^2).^.5)+...
                ((main(1,1)-main(1,end))^2+(main(2,1)-main(2,end))^2)^.5;
            end
             if(cur<opt)
                 opt = cur;
%                  disp([' opt=' num2str(opt)])
                 whileTrue = true;
             else
                tmp = main(:,j);
                main(:,j) = main(:,i);
                main(:,i) = tmp;
                tmp = ind(j);ind(j)=ind(i);ind(i)=tmp;
             end
        end
    end
    for i=1:length(x)
        for j=2:length(x)
            main(:,i:j)=fliplr(main(:,i:j));
            ind(i:j) = fliplr(ind(i:j));
            if (normStreet)
                cur=sum(abs(main(1,1:end-1)-main(1,2:end))+abs(main(2,1:end-1)-main(2,2:end)))+...
                abs(main(1,1)-main(1,end))+abs(main(2,1)-main(2,end));
            else
                cur=sum(((main(1,1:end-1)-main(1,2:end)).^2+(main(2,1:end-1)-main(2,2:end)).^2).^.5)+...
                ((main(1,1)-main(1,end))^2+(main(2,1)-main(2,end))^2)^.5;
            end
             if(cur<opt)
                 opt = cur;
%                  disp(['opt=' num2str(opt)])
                 whileTrue = true;
             else
                main(:,i:j)=fliplr(main(:,i:j));
                ind(i:j) = fliplr(ind(i:j));

             end
        end
    end 
end