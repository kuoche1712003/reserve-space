package io.kuoche.model.bo

import java.time.LocalTime
import java.util.BitSet

class HourOfDate(): BitSet(LENGTH) {
    companion object{
        val LENGTH = 48
    }

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
        val startHour = start.toSecondOfDay() / 60 / 60
        val endHour = end.toSecondOfDay() / 60 / 60
        set(startHour, endHour)
    }


    fun toBinaryString(): String{
        val builder = StringBuilder(LENGTH)
        (0 until LENGTH)
            .map { if(get(it)) '1' else '0' }
            .forEach(builder::append)
        return builder.toString()
    }

    fun toLong(): Long{
        val array = this.toLongArray()
        return if(array.size == 0){
            0L
        }else{
            array[0]
        }
    }
}