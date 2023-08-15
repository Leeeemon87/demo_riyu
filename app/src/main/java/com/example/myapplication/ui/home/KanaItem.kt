package com.example.myapplication.ui.home

class KanaItem(val honmei: String, val furikana: String, val accent: String) {
    val n=furikana.length
    val type=accent.toInt()
    val acs= comp(n,type)

    private fun comp(n:Int,type:Int):MutableList<Int> {
        val res = mutableListOf<Int>()
        if(type==0){
            res.add(0)
            for(i in 1 until n){
                res.add(1)
            }
        } else if (type==1){
            res.add(1)
            for (i in 1 until n){
                res.add(0)
            }
        } else{
            res.add(0)
            for(i in 1 until type){
                res.add(1)
            }
            for(i in type until n){
                res.add(0)
            }
        }
        return res
    }
}