package com.example.myapplication.ui.home

import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class KanaItem(val smallKana:List<String>,val honmei: String, val furikana: String, val accent: String) {
    val kana=getKana(furikana)
    val n=kana.size
    val type=accent.toInt()
    val acs= comp(n,type)

    private  fun getKana(furikana:String):MutableList<String> {
        val kanas= mutableListOf<String>()
        for (i in furikana.indices){
            if (furikana[i].toString() in smallKana){
                if (kanas.isNotEmpty()) {
                    val lastIndex = kanas.lastIndex
                    val lastElement = kanas[lastIndex]
                    val updatedLastElement = lastElement + furikana[i].toString()
                    kanas[lastIndex] = updatedLastElement
                } else {
                    kanas.add(furikana[i].toString())
                }
            }
            else{
                kanas.add(furikana[i].toString())
            }
        }
        return kanas
    }

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