package io.kuoche.model.bo

import java.time.LocalTime
import java.util.BitSet

class MinuteOfDate(): BitSet(LENGTH) {
    constructor(binaryString: String): this(){
        if(binaryString.length != LENGTH){
            throw IllegalArgumentException()
        }
        binaryString.toCharArray().forEachIndexed { index, c ->
            if(c == '1'){
                set(index)
            }
        }

    }
    constructor(start: LocalTime, end: LocalTime): this(){
        val startMinutes = start.toSecondOfDay() / 60
        val endMinutes = end.toSecondOfDay() / 60
        set(startMinutes, endMinutes)
    }


    companion object{
        const val LENGTH = 1440
        val EMPTY: MinuteOfDate = MinuteOfDate()
    }


    fun toBinaryString(): String{
        val builder = StringBuilder(LENGTH)
        (0 until LENGTH)
            .map { if(get(it)) '1' else '0' }
            .forEach(builder::append)
        return builder.toString()
    }

    fun emptyTime(): List<List<LocalTime>>{
        val result = mutableListOf<List<LocalTime>>()
        var start: Int? = null
        for(i: Int in 0 until LENGTH){
            if(!get(i) && start == null){
                start = i
            }else if(get(i) && start != null){
                val startSecond = start * 60L
                val endSecond = (i - 1) * 60L
                val section = listOf(LocalTime.ofSecondOfDay(startSecond), LocalTime.ofSecondOfDay(endSecond))
                result.add(section)
                start = null
            }
        }
        return result
    }
}

