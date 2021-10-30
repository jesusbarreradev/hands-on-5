import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlgGenetico {
    private static int CantidadInd = 20;
    private static int CantidadIte = 1000000;
    private static ArrayList<Individuo> poblacion;
    private static ArrayList<Individuo> supervivientes;

    public static class Individuo
    {
        private long Genoma;
        private float Puntaje;
        private float Probabilidad;
        private float ProbabilidadAcumulada;

        public String getGenoma()
        {
            return DecimalToBinario(Genoma);
        }

        public void setGenoma(StringBuilder n)
        {
            this.Genoma = BinarioToDecimal(String.valueOf(n));
        }

        public String DecimalToBinario(long num)
        {
            if(num<0) //absoluto
            {
                num = num * -1;
            }
            long n = num;
            String bin = "";

            if(n == 0)
            {
                return "00000000";
            }

            while(n>0)
            {
                if(n%2 == 0)
                    bin = "0" + bin;
                else
                    bin = "1" + bin;
                n = n/2;
            }

            while (bin.length() < 8) //FORZAR 8 bits
            {
                bin = "0" + bin;
            }

            return bin;
        }

        public long BinarioToDecimal(String binario)
        {
            long decimal = 0;
            int posicion = 0;
            // Recorrer la cadena...
            for (int x = binario.length() - 1; x >= 0; x--) {
                // Saber si es 1 o 0; primero asumimos que es 1 y abajo comprobamos
                short digito = 1;
                if (binario.charAt(x) == '0') {
                    digito = 0;
                }

              /*
                  Se multiplica el dígito por 2 elevado a la potencia
                  según la posición; comenzando en 0, luego 1 y así
                  sucesivamente
               */
                double multiplicador = Math.pow(2, posicion);
                decimal += digito * multiplicador;
                posicion++;
            }
            return decimal;
        }
    }

    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static void CrearPoblacion()
    {
        poblacion = new ArrayList<>();
        supervivientes = new ArrayList<>();
        for (int i = 0; i < CantidadInd; i++)
        {
            Individuo ind = new Individuo();
            ind.Genoma = getRandomNumberUsingNextInt(1, 255); //B0 255
            poblacion.add(ind);
        }
    }

    public static void Organizar(){
        //Metodo de burbuja
        boolean sw = false;
        while (!sw)
        {
            sw = true;
            for (int i = 1; i < poblacion.size(); i++)
            {
                if (poblacion.get(i).Puntaje > poblacion.get(i - 1).Puntaje)
                {
                    Individuo ind = poblacion.get(i);
                    poblacion.set(i, poblacion.get(i - 1));
                    poblacion.set(i - 1, ind);
                    sw = false;
                }
            }
        }
    }

    static double MiFuncion(boolean mayor, float x)
    {
        if(mayor == false)
        {
            return x/168;
        }
        else
        {
            return 168/x;
        }
    }

    public static void DeterminarPuntajes(){
        double genoma;
        boolean mayor = true;
        for (int i = 0; i < poblacion.size(); i++)
        {
            mayor = true;
            Individuo ind = poblacion.get(i);
            if(poblacion.get(i).Genoma < 168)
                mayor = false;
            ind.Puntaje = (float) (MiFuncion(mayor, poblacion.get(i).Genoma));
            poblacion.set(i, ind);
        }
    }

    public static void Mostrar()
    {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < poblacion.size(); i++)
        {
            s.append(" ").append("(").append(i).append(")").append(poblacion.get(i).Genoma).append(" p:").append(poblacion.get(i).Puntaje);
        }
        System.out.println(s);
    }

    static void Combinacion()
    {
        float puntaje, puntajeAcum = 0, puntajeAcumInd = 0, buff=0;

        //se determina el acumulado del porcentaje
        for(int i = 0; i < poblacion.size(); i++)
        {
            puntaje = poblacion.get(i).Puntaje;
            puntajeAcum = puntajeAcum + puntaje;
        }
        for(int i = 0; i < poblacion.size(); i++)
        {
            Individuo ind = poblacion.get(i);
            puntaje = poblacion.get(i).Puntaje;
            puntajeAcumInd = puntaje/puntajeAcum;
            ind.Probabilidad = puntajeAcumInd;
            buff = puntajeAcumInd + buff;
            ind.ProbabilidadAcumulada = buff;
            poblacion.set(i,ind);
        }

        Random r = new Random();
        float randRuletaNum = 0 + r.nextFloat() * (poblacion.get(poblacion.size()-1).ProbabilidadAcumulada - 0);
        float randRuletaNum2 = 0 + r.nextFloat() * (poblacion.get(poblacion.size()-1).ProbabilidadAcumulada - 0);
        float bufferRuleta = 0, probabilidadActual;
        long temporal;
        long tempP2 = 0;
        String Padre1 = "N"; //01010101  →  010101 10
        String Padre2 = "N"; //10101010  →  101010 01
        boolean Padre1Listo = false;
        boolean Padre2Listo = false;
        for(int i = 0; i < poblacion.size(); i++)
        {
            Individuo ind = poblacion.get(i);
            probabilidadActual = poblacion.get(i).ProbabilidadAcumulada;
            if((bufferRuleta<randRuletaNum && randRuletaNum<=probabilidadActual) && Padre1Listo == false)
            {
                temporal = poblacion.get(i).Genoma;
                Padre1 = ind.DecimalToBinario(temporal);
                Padre1Listo = true;
            }
            else
            {
                if((bufferRuleta<randRuletaNum2 && randRuletaNum2<=probabilidadActual) && Padre2Listo == false)
                {
                    temporal = poblacion.get(i).Genoma;
                    Padre2 = ind.DecimalToBinario(temporal);
                    Padre2Listo = true;
                }
                else {
                    bufferRuleta = probabilidadActual;
                }
            }
            if(i == poblacion.size()-1)
            {
                long siFalla=0;
                if(Padre1 == "N")
                {
                    siFalla = poblacion.get(getRandomNumberUsingNextInt(0, poblacion.size()-1)).Genoma;
                    Padre1 = ind.DecimalToBinario(siFalla);
                }
                if(Padre2 == "N")
                {
                    siFalla = poblacion.get(getRandomNumberUsingNextInt(0, poblacion.size()-1)).Genoma;
                    Padre2 = ind.DecimalToBinario(siFalla);
                }
            }
        }

        int mutacion = getRandomNumberUsingNextInt(0,Padre1.length());
        int contador;
        StringBuilder Hijo1 = new StringBuilder(Padre1.length());
        StringBuilder Hijo2 = new StringBuilder(Padre1.length());

        //Combinacion
        for(contador = 0; contador < mutacion; contador++)
        {
            Hijo1.append(Padre2.charAt(contador));
            Hijo2.append(Padre1.charAt(contador));
        }
        for(int cont2 = contador; cont2 < Padre1.length(); cont2++)
        {
            Hijo1.append(Padre1.charAt(cont2));
            Hijo2.append(Padre2.charAt(cont2));
        }

        Individuo ind = new Individuo();
        ind.Genoma = ind.BinarioToDecimal(String.valueOf(Hijo1)); //B0 255
        supervivientes.add(ind);
        Individuo ind2 = new Individuo();
        ind2.Genoma = ind2.BinarioToDecimal(String.valueOf(Hijo2)); //B0 255
        supervivientes.add(ind2);
    }

    static void Mutacion(){
        long GenomaHijo;
        StringBuilder SGenoma;
        float ratioMutacion = 0.01f;
        Random r = new Random();
        for(int i=0; i< supervivientes.size(); i++)
        {
            float randRatioMutacion = 0 + r.nextFloat() * (1 - 0);
            if(randRatioMutacion < ratioMutacion)
            {
                Individuo ind = supervivientes.get(i);
                GenomaHijo = supervivientes.get(i).Genoma;
                SGenoma = new StringBuilder(ind.DecimalToBinario(GenomaHijo));
                int rand = getRandomNumberUsingNextInt(0, SGenoma.length());
                char bit = SGenoma.charAt(rand);
                if (bit == '1') {
                    SGenoma.setCharAt(rand, '0');
                } else {
                    SGenoma.setCharAt(rand, '1');
                }
                ind.Genoma = ind.BinarioToDecimal(String.valueOf(SGenoma)); //B0 255
            }
        }
    }

    static void ReemplazarPoblacion()
    {
        poblacion = new ArrayList<>();
        for(int i = 0; i < supervivientes.size(); i++)
        {
            poblacion.add(supervivientes.get(i));
        }
        supervivientes = new ArrayList<>();
    }

    public static void Start(){
        CrearPoblacion();
        int ite = 0;
        for (int i = 0; i < CantidadIte; i++) //posible a modificar
        {
            DeterminarPuntajes();
            Organizar();
            Mostrar();
            int j =0, contGenoma = 0;
            while(j < poblacion.size())
            {
                if(poblacion.get(j).Genoma == 168)
                {
                    contGenoma++;
                }
                j++;
            }
            if(contGenoma == poblacion.size())
            {
                break;
            }
            else
            {
                while (supervivientes.size() < poblacion.size()) {
                    Combinacion();
                }
                Mutacion();
                ReemplazarPoblacion();
                ite++;
            }
        }
        System.out.println("FIN  " + ite++);
    }

    public static void main(String[] args) {
        Start();
    }
}
