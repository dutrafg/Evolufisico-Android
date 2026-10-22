package org.json
/** Compile-time-only API shims. Do NOT ship into the Android application. */
class JSONObject {
    constructor()
    constructor(text:String)
    fun put(k:String,v:Any?):JSONObject=this
    fun getString(k:String):String=""
    fun optString(k:String):String=""
    fun optString(k:String,default:String):String=""
    fun optDouble(k:String):Double=Double.NaN
    fun optInt(k:String):Int=0
    fun optInt(k:String,default:Int):Int=default
    fun optLong(k:String):Long=0L
    fun optBoolean(k:String):Boolean=false
    fun optJSONObject(k:String):JSONObject?=null
    fun optJSONArray(k:String):JSONArray?=null
    fun getJSONObject(k:String):JSONObject=JSONObject()
    fun getJSONArray(k:String):JSONArray=JSONArray()
    fun getDouble(k:String):Double=Double.NaN
    fun getInt(k:String):Int=0
    fun getLong(k:String):Long=0L
    fun has(k:String):Boolean=false
    fun isNull(k:String):Boolean=false
    fun keys():MutableIterator<String> = mutableListOf<String>().iterator()
    companion object {val NULL=Any()}
}
class JSONArray {
    constructor()
    constructor(text:String)
    fun put(value:Any?):JSONArray=this
    fun length():Int=0
    fun getJSONObject(i:Int):JSONObject=JSONObject()
}
