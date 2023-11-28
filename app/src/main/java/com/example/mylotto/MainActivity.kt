package com.example.mylotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity() {
    //이벤트 들어가는 4가지 버튼 정의
    private val clearButton by lazy { findViewById<Button>(R.id.btn_clear) } //버튼 선언 하고 lazy = 버튼의 값을 호출할때까지 정하지 않는다 null을 사용하면 오류가 많이나서 null로 정의하지않고 lazy로 값을 늦게 정한다
    private val addButton by lazy { findViewById<Button>(R.id.btn_add) }
    private val runButton by lazy { findViewById<Button>(R.id.btn_run) }
    private val numPick by lazy { findViewById<NumberPicker>(R.id.np_num) }

    //6개 공 리스트 목록을 만들어 하나씩 꺼내쓰기
    private val numTextViewList : List<TextView> by lazy {
        listOf<TextView>(
            findViewById(R.id.tv_num1),
            findViewById(R.id.tv_num2),
            findViewById(R.id.tv_num3),
            findViewById(R.id.tv_num4),
            findViewById(R.id.tv_num5),
            findViewById(R.id.tv_num6)
        )
    }

    //지금 run상태인지 체크 var 가변 val 불변
    private var didRun = false //현재 실행중인지 아닌지
    private val pickNumberSet = hashSetOf<Int>() //로또 사용자가 지정한숫자 임시로 담아 둘 곳


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numPick.minValue = 1
        numPick.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()

    }

    private fun initAddButton() {
        addButton.setOnClickListener{
            //생각할것
            //1번호 꽉차있을때 초기화 해주세요 메세지 뜨게 하기
            //5번까진 추가하되 6번까지 차면 자동생성이 불가능하니 5번까지만 추가가능하게
            //중복번호 막기

            when {
                didRun -> showToast("초기화 후에 시도해주세요.")//박스 자동생성 눌러서 꽉차잇냐
                pickNumberSet.size >=5 -> showToast("숫자는 최대 5개까지 선택할 수 있습니다.")
                pickNumberSet.contains(numPick.value) -> showToast("이미 선택된 숫자입니다")//픽넘버셋에 이미 넣은 숫자가 포함되있냐? 넘버피커 값에서 확인해보기
                else -> {
                    val textView = numTextViewList[pickNumberSet.size] //픽넘버셋에 들어있는거 만큼 리스트에서 꺼내오는것
                    textView.isVisible = true //가린거 보여주고
                    textView.text = numPick.value.toString() //공에 숫자 쓰기

                    setNumBack(numPick.value, textView)
                    pickNumberSet.add(numPick.value)

                }
            }
        }
    }

    private fun initClearButton() {

        clearButton.setOnClickListener{
            pickNumberSet.clear()
            numTextViewList.forEach{it.isVisible = false}//반복문
            didRun = false
            numPick.value = 1

        }
    }

    private fun  initRunButton(){
        runButton.setOnClickListener{
            val list = getRandom() //6개의 랜덤값뽑아옴 이제 보여줘야지

            didRun = true

            list.forEachIndexed{ index, number ->
                val textView = numTextViewList[index]
                textView.text = number.toString()
                textView.isVisible = true
                setNumBack(number, textView)
            }
        }
    }

    private fun getRandom() : List<Int> {
        val numbers = (1..45).filter{ it !in pickNumberSet} //1부터 45사이의 사용자가 먼저 선택한 숫자를 뺀 나머지

        return (pickNumberSet + numbers.shuffled().take(6-pickNumberSet.size)).sorted() //shuffled섞고 기존에 사용자가 선택한 숫자를 더해주고 테이크 할껀대 먼저 선택한 숫자만큼 사이즈를 6에서 빼주기
    }

    private fun setNumBack(number:Int, textView: TextView){ //숫자를 받고 텍스트뷰를 받기 색깔선택해서 줄것
        val background = when(number) { //1~10까지는 노란색 11~20까진 파란색...
            in 1..10 -> R.drawable.circle_yellow
            in 11..20 -> R.drawable.circle_blue
            in 21..30 -> R.drawable.circle_red
            in 31..40 -> R.drawable.circle_gray
            else -> R.drawable.circle_green

        }

        textView.background = ContextCompat.getDrawable(this,background)
    }

    private fun showToast(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }//스트링타입의 메세지 받기
}