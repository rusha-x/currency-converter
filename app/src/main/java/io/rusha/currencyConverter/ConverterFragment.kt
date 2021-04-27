package io.rusha.currencyConverter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConverterFragment : Fragment(R.layout.converter_fragment) {
    var conversions: Conversions? = null

    //Реализация отмены всех сетевых запросов с RxJava
    private val disposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://open.exchangerate-api.com/v6/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        val api = retrofit.create(ConverterApi::class.java)

        //Каждый запрос нужно записать в disposable
        disposable.add(
            api.getConversions()                          // Получаем Single1 с запросом
                .subscribeOn(Schedulers.io())             // Оборачиваем Single1 в S2, который выполнит запрос в одном из потоков io
                .observeOn(AndroidSchedulers.mainThread())// S2 оборачиваем в S3, определяем в каком потоке обработка ответов
                .subscribe(                               // Запускаем S3 и подписываемся на его результат
                    { serverConversions ->                // Калбэк на успешный запрос
                        conversions = serverConversions
                    },
                    { e ->                                // Калбэк на ошибку
                        println(e)
                    }
                )
        )

        val fromCurrencyText = view.findViewById<TextView>(R.id.fromCurrencyText)
        val toCurrencyText = view.findViewById<TextView>(R.id.toCurrencyText)
        fromCurrencyText.setOnClickListener { showAlert(fromCurrencyText) }
        toCurrencyText.setOnClickListener { showAlert(toCurrencyText) }

        val fromInput = view.findViewById<TextInputEditText>(R.id.fromCurrencyInput)
        val toInput = view.findViewById<TextInputEditText>(R.id.toCurrencyInput)
        fromInput.addTextChangedListener { text ->
            val input = text.toString().toDoubleOrNull()
            if (input != null) {
                toInput.setText(
                    conversions?.convert(
                        input = input,
                        fromCurrencyText.text.toString(), toCurrencyText.text.toString()
                    ).toString()
                )
            }
        }
    }

    private fun showAlert(currencyText: TextView) {
        val alertView = LayoutInflater.from(requireActivity()).inflate(R.layout.select_currency_alert, null, false)
        val alert = AlertDialog.Builder(requireActivity())
            .setView(alertView)
            .setTitle("Choose a currency")
            .setNegativeButton("Cancel") { _, _ ->
            }
            .show()

        val recyclerView: RecyclerView = alertView.findViewById(R.id.currencyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = CurrenciesAdapter(
            values = conversions?.rates?.keys?.toList() ?: listOf(),
            onItemClickListener = { currency ->
                currencyText.text = currency
                alert.cancel()
            },
            current = currencyText.text.toString()
        )
    }

    override fun onStop() {
        super.onStop()

        //Отменяем все запросы, когда фрагмент перестает быть видимым
        disposable.dispose()
    }

}

