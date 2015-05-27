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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 5/21/15.
 */
@Service
public class HolidayHandler {
    @Autowired
    private HolidayRepository holidayRepository;
    private Set<Long> holidays =new TreeSet<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private static HolidayHandler handler;

    @PostConstruct
    private void init(){
        handler = this;
    }

    private void reLoad(){
        lock.writeLock().lock();
        try {
            holidays.clear();
            Iterable<HolidayEntity> entities = holidayRepository.findAll();
            for (HolidayEntity entity : entities) {
                holidays.add(entity.getDay().getTime());
            }
        }finally {
            lock.writeLock().unlock();
        }
    }
    private Collection<Long> getHolidays(){
        lock.readLock().lock();
        try{
            if(!holidays.isEmpty()){
                return holidays;
            }
        }finally {
            lock.readLock().unlock();
        }

        reLoad();
        lock.readLock().lock();
        try{
            return holidays;
        }finally {
            lock.readLock().unlock();
        }
    }
    public static boolean isHoliday(Calendar day) {
        return DateHelper.isWeekend(day) || handler.getHolidays().contains(day.getTimeInMillis());
    }
}
