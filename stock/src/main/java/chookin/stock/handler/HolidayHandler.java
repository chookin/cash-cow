package chookin.stock.handler;

import chookin.stock.orm.domain.HolidayEntity;
import chookin.stock.orm.repository.HolidayRepository;
import cmri.utils.lang.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by zhuyin on 5/21/15.
 */
@Service
public class HolidayHandler {
    @Autowired
    private HolidayRepository holidayRepository;
    private Set<Long> holidays =new TreeSet<>();

    private static HolidayHandler handler;

    @PostConstruct
    private void init(){
        handler = this;
        Iterable<HolidayEntity> entities = holidayRepository.findAll();
        for(HolidayEntity entity: entities){
            holidays.add(entity.getDay().getTime());
        }
    }
    private static Collection<Long> getHolidays(){
        return handler.holidays;
    }
    public static boolean isHoliday(Calendar day){
        if(DateHelper.isWeekend(day)){
            return true;
        }
        return getHolidays().contains(day.getTimeInMillis());
    }
}
