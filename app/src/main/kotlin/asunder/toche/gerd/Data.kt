package asunder.toche.gerd
/**
 * Created by ToCHe on 10/24/2017 AD.
 */

object Data{

    val  locations = ArrayList<String>().apply {
        add("ภาคเหนือ")
        add("ภาคใต้")
        add("ภาคตะวันออก")
        add("ภาคตะวันตก")
        add("ภาคตะวันออกเฉียงเหนือ")
    }
    val totalRain = ArrayList<String>().apply {
        add("3")
        add("4")
        add("5")
        add("7")
    }

/*
    North	South	East	West	Norhteast
    x	y	x	y	x	y	x	y	x	y
    0	160	0	365	0	160	0	160	0	160
    360	0	545	0	360	0	360	0	360	0
   	0	135	0	250	0	135	0	135	0	135
   	305	0	370	0	305	0	305	0	305	0
    */

    val SouthRain :MutableList<Model.valRain> = ArrayList<Model.valRain>().apply {
        add(Model.valRain(0f, 365f))
        add(Model.valRain(545f, 0f))
        add(Model.valRain(0f, 250f))
        add(Model.valRain(370f, 0f))
        //offset  red = 4  yellow = 5
        add(Model.valRain(0.0f, 0.0f))
        add(Model.valRain(0.0f, 0.0f))
    }

    val EastRain :MutableList<Model.valRain> = ArrayList<Model.valRain>().apply {
        add(Model.valRain(0f, 160f))
        add(Model.valRain(360f, 0f))
        add(Model.valRain(0f, 135f))
        add(Model.valRain(305f, 0f))
        //offset  red = 4  yellow = 5
        add(Model.valRain(0.0f, 0.0f))
        add(Model.valRain(0.0f, 0.0f))
    }

    val WestRain :MutableList<Model.valRain> = ArrayList<Model.valRain>().apply {
        add(Model.valRain(0f, 160f))
        add(Model.valRain(360f, 0f))
        add(Model.valRain(0f, 135f))
        add(Model.valRain(305f, 0f))
        //offset  red = 4  yellow = 5
        add(Model.valRain(0.0f, 0.0f))
        add(Model.valRain(0.0f, 0.0f))
    }

    val NorthEastRain :MutableList<Model.valRain> = ArrayList<Model.valRain>().apply {
        add(Model.valRain(0f, 160f))
        add(Model.valRain(360f, 0f))
        add(Model.valRain(0f, 135f))
        add(Model.valRain(305f, 0f))
        //offset  red = 4  yellow = 5
        add(Model.valRain(0.0f, 0.0f))
        add(Model.valRain(0.0f, 0.0f))
    }

    val NorthRain :MutableList<Model.valRain> = ArrayList<Model.valRain>().apply {
        add(Model.valRain(0f, 160f))
        add(Model.valRain(360f, 0f))
        add(Model.valRain(0f, 135f))
        add(Model.valRain(305f, 0f))
        //offset  red = 4  yellow = 5
        add(Model.valRain(0.0f, 0.0f))
        add(Model.valRain(0.0f, 0.0f))
    }


}