function cnt = Count_crossing(x,y,xA,yA,xB,yB,a,b,c,d)
% cnt is 0 or 1
cnt=0;
if(isnan(a))
	error('NaN a b c d params');
end
% y-Direction
if (isnan(c))
    if (xA~=xB)
        error('wrong y-Direction');
    end
    if(xA<x && yA<y && y<yB)
        cnt=1;
    end
    if(xA<x && yB<y && y<yB)
        cnt=1;
    end
    return;
end
if (a~=yB)
    error('wrong a b c d params');
end
% ignore boundarey case, yA=yB
if (xA<=xB)
    if (xA<x && xB<x)
        if(yA<=y && yB<=y)
            cnt=0;
        elseif (yA>=y && yB>=y)
            cnt = 0;
        elseif (yB<y && y<yA)
            cnt=1;
        elseif (yA<y && y<yB)
            cnt=1;
        end
        %disp(['xA<x && xB<x cnt=' num2str(cnt) 'yA=' num2str(yA) ' yB=' num2str(yB) ' y=' num2str(y)])
    elseif(xA<x && x<=xB)
        yp = d/6*(x-xB)*(x-xB)*(x-xB)+c/2*(x-xB)*(x-xB)+b*(x-xB)+a;
        if (yA<yB)
            if (yA<y && y<yp)
                cnt = 1;
            end
        elseif(yB<yA)
            if(yp<y && y<yA)
                 cnt=1;
            end
        end
    end
else
%	error('wrong order');
    if (xA<x && xB<x)
        if(yA<=y && yB<=y)
            cnt=0;
        elseif (yA>=y && yB>=y)
            cnt = 0;
        elseif (yB<y && y<yA)
            cnt=1;
        elseif (yA<y && y<yB)
            cnt=1;
        end
        %disp([' xA<x && xB<x cnt=' num2str(cnt) 'yA=' num2str(yA) ' yB=' num2str(yB) ' y=' num2str(y)])
    elseif(xB<x && x<=xA)
        %TODO make it avalable
        yp = d/6*(x-xB)*(x-xB)*(x-xB)+c/2*(x-xB)*(x-xB)+b*(x-xB)+a;
        if (yB<yA)
            if (yB<y && y<yp)
                cnt = 1;
            end
        elseif(yA<yB)
            if(yp<y && y<yB)
                cnt=1;
            end
        end
    end
end

end