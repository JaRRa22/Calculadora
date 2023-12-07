package com.ingentc.calculadora_pro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ingentc.calculadora_pro.databinding.ActivityMainBinding
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var punto : Boolean = true
    private val puntoChar = "."
    private val igual = "="
    private val masMenos = "-"
    private val error = "Operación inválida"
    private var operacion1 : Boolean = false

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)
        binding.btn9.setOnClickListener(this)
        binding.btnComa.setOnClickListener(this)
        binding.btnMasMenos.setOnClickListener(this)
        binding.borrar.setOnClickListener(this)
        binding.borrarTodo.setOnClickListener(this)
        binding.btnIgual.setOnClickListener(this)
        binding.btnSumar.setOnClickListener(this)
        binding.btnRestar.setOnClickListener(this)
        binding.btnMultiplicar.setOnClickListener(this)
        binding.btnDividir.setOnClickListener(this)
        binding.potencia.setOnClickListener(this)
        binding.raiz.setOnClickListener(this)


    }


    override fun onClick(v: View?) {
        when(v){
            binding.btn0 -> presionarNumero("0")
            binding.btn1 -> presionarNumero("1")
            binding.btn2 -> presionarNumero("2")
            binding.btn3 -> presionarNumero("3")
            binding.btn4 -> presionarNumero("4")
            binding.btn5 -> presionarNumero("5")
            binding.btn6 -> presionarNumero("6")
            binding.btn7 -> presionarNumero("7")
            binding.btn8 -> presionarNumero("8")
            binding.btn9 -> presionarNumero("9")
            binding.btnComa -> presionarPunto()
            binding.btnMasMenos -> presionarMasMenos()
            binding.borrar -> presionarBorrarDato()
            binding.borrarTodo -> presionarBorrarTodo()
            binding.btnIgual -> pulsarIgual()
            binding.btnSumar -> presionarOperacion("+")
            binding.btnRestar -> presionarOperacion("-")
            binding.btnMultiplicar -> presionarOperacion("x")
            binding.btnDividir -> presionarOperacion("/")
            binding.potencia -> presionarOperacion("^")
            binding.raiz -> presionarOperacion("√")
        }

    }

    private fun presionarBorrarDato(){
        binding.insertarNums.text = "0"
        punto = true
    }

    private fun presionarBorrarTodo(){
        binding.insertarNums.text = "0"
        binding.historial.text = ""
        punto = true
    }

    private fun presionarNumero(number: String){
        ponerEnInsertarNums(number)
    }
    private fun presionarPunto(){
        if (punto){
            val resultado = "${binding.insertarNums.text}$puntoChar"
            binding.insertarNums.text = resultado
            punto = false
        }
    }
    private fun presionarMasMenos(){
        val resultado = if (!binding.insertarNums.text.startsWith("-")){
            "${masMenos}${binding.insertarNums.text}"
        }
        else{
            binding.insertarNums.text
        }

        binding.insertarNums.text = resultado
    }

    private fun ponerEnInsertarNums(number: String){

        val resultado = if(binding.insertarNums.text == "0" && number != ".")
            number
        else
            "${binding.insertarNums.text}$number"

        binding.insertarNums.text = resultado
        operacion1 = true
    }

    private fun presionarOperacion(operacion: String){
        if (operacion1){
            binding.insertarNums.text = buildString {
                append(binding.insertarNums.text)
                append(operacion)
            }
        }
        punto = true
        operacion1 = false
    }

    private fun pulsarIgual(){
        try {
            val vacio = ""
            binding.historial.text = vacio
            var cadena = binding.insertarNums.text as String
            val historial = "${binding.historial.text}$cadena$igual"
            binding.historial.text = historial

            while (cadena.contains("x") || cadena.contains("/") || cadena.contains("^") || cadena.contains("√")) {
                if (cadena.startsWith("√")) {
                    cadena = "0$cadena"
                }
                cadena = sacarOperacionesPrioritarias(cadena)
            }


            while (cadena.contains("+") || cadena.contains("-")) {
                if (cadena.startsWith("-") && contadorDeOperaciones(cadena) == 1) {
                    break
                }
                cadena = sacarOperacionesSinPrioridad2(cadena)
            }

            binding.insertarNums.text = cadena
        }
        catch (e : Exception){
            presionarBorrarTodo()
            binding.insertarNums.text = error
        }

    }

    private fun esOperacion(operacion: String): Boolean {
        return operacion == "+" || operacion == "-" || operacion == "x" || operacion == "/" || operacion == "^" || operacion == "√"
    }

    private fun sacarOperacionesPrioritarias(cadena: String): String {
        val aux: String
        val sub1: String
        val sub2: String
        val subOperacion: String
        var left: Int
        var right: Int
        for (i in cadena.indices) {
            if (cadena[i] == 'x' || cadena[i] == '/' || cadena[i] == '^' || cadena[i] == '√') {
                subOperacion = cadena[i].toString()
                left = i - 1
                right = i + 1
                while (!esOperacion(cadena[left].toString()) && left > 0) {
                    left--
                }
                if (left > 0) {
                    left++
                }
                while (!esOperacion(cadena[right].toString()) && right < cadena.length - 1) {
                    right++
                }
                if (right == cadena.length - 1) {
                    right++
                }
                aux = cadena.substring(left, right)
                val resultado = resultadoOperacion(
                    aux.substring(0, aux.indexOf(subOperacion)),
                    subOperacion,
                    aux.substring(aux.indexOf(subOperacion) + 1)
                )
                sub1 = cadena.substring(0, left)
                sub2 = cadena.substring(right)
                return sub1 + resultado + sub2
            }
        }
        return cadena
    }

    private fun sacarOperacionesSinPrioridad2(input: String): String {
        var cadena = input
        val aux: String
        val sub1: String
        val sub2: String
        val subOperacion: String
        val comprobarNegativo = cadena
        var menos = ""
        val resultado: String
        var left: Int
        var right: Int
        if (cadena.startsWith("-")) {
            menos = "-"
            cadena = cadena.substring(1)
        }
        for (i in cadena.indices) {
            if (cadena[i] == '+' || cadena[i] == '-') {
                subOperacion = cadena[i].toString()
                left = i - 1
                right = i + 1

                while (!esOperacion(cadena[left].toString()) && left > 0) {
                    left--
                }

                if (left > 0) {
                    left++
                }

                while (!esOperacion(cadena[right].toString()) && right < cadena.length - 1) {
                    right++
                }

                if (right == cadena.length - 1) {
                    right++
                }

                aux = cadena.substring(left, right)
                val mitadLeft = aux.substring(0, aux.indexOf(subOperacion))
                val mitadRight = aux.substring(aux.indexOf(subOperacion) + 1)

                resultado = if (menos.startsWith("-")) {
                    val mitadLeftNegativa = menos + mitadLeft
                    resultadoOperacion(mitadLeftNegativa, subOperacion, mitadRight)
                } else {
                    resultadoOperacion(mitadLeft, subOperacion, mitadRight)
                }

                sub1 = cadena.substring(0, left)
                sub2 = cadena.substring(right)
                return sub1 + resultado + sub2
            }
        }
        return if (comprobarNegativo.startsWith("-")) {
            menos + cadena
        } else {
            cadena
        }
    }

    private fun resultadoOperacion(mitadLeft: String, operador: String?, mitadRight: String): String {
        var resultado = 0.0
        when (operador) {
            "+" -> resultado = mitadLeft.toDouble() + mitadRight.toDouble()
            "-" -> resultado = mitadLeft.toDouble() - mitadRight.toDouble()
            "x" -> resultado = mitadLeft.toDouble() * mitadRight.toDouble()
            "/" -> resultado = mitadLeft.toDouble() / mitadRight.toDouble()
            "√" -> resultado = sqrt(mitadRight.toDouble())
            "^" -> resultado = mitadLeft.toDouble().pow(mitadRight.toDouble())
        }
        val resultadoCadena = resultado.toString()
        return if (resultadoCadena.contains(".0")) {
            resultadoCadena.substring(0, resultadoCadena.length - 2)
        } else resultadoCadena
    }

    private fun contadorDeOperaciones(cadena: String): Int {
        var contador = 0
        for (i in cadena.indices) {
            if (cadena[i] == '+' || cadena[i] == '-') {
                contador++
            }
        }
        return contador
    }



}