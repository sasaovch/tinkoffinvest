using System;
using System.Collections.Generic;
using System.Text;
using WealthLab;
using WealthLab.Indicators;
using System.Drawing;
 
namespace WealthLabCompile
{
    class MovingAverageCrossover : WealthScript
    {
        private StrategyParameter slowPeriod; //Объявляем период для медленной скользящей средней.
        private StrategyParameter fastPeriod; //Объявляем период для быстрой скользящей средней..
 
        public MovingAverageCrossover() //Создаём конструктор для инициализации переменных
        {
            fastPeriod = CreateParameter("Fast Period", 20, 1, 100, 1); //Инициализируем быструю скользящая среднею.
            slowPeriod = CreateParameter("Slow Period", 50, 20, 300, 5); //Инициализируем медленную скользящая среднею.      
        }
 
        protected override void Execute()
        {
            int fastPer = fastPeriod.ValueInt; //Получаем значение периода для быстрой скользящей средней
            int slowPer = slowPeriod.ValueInt; //Получаем значение периода для медленной скользящей средней
       
            SMA smaFast = SMA.Series(Close, fastPer); //Создаём простую скользящая среднею с быстрым периодом
            SMA smaSlow = SMA.Series(Close, slowPer); //Создаём простую скользящая среднею с медленным периодом
 
            PlotSeries(PricePane, smaFast, Color.Green, LineStyle.Solid, 2); //Наносим скользящая на график
            PlotSeries(PricePane, smaSlow, Color.Red, LineStyle.Solid, 2); //Наносим скользящая на график
 
            for (int bar = Math.Max(fastPer, slowPer); bar < Bars.Count; bar++) //Главный цикл который последовательно перебирает все доступные данные. 
            {
                if (IsLastPositionActive) //Проверяем существует ли открытая позиция  Если да то
                {         
                    if (CrossOver(bar, smaFast, smaSlow)) //Не произошло ли пересечения скользящих снизу вверх если произошло то    
                    {
                        ExitAtMarket(bar + 1, LastPosition); //Закрываем открытую короткую позицию
                        BuyAtMarket(bar + 1); //Открываем длинную  позицию
                    }
                    if (CrossUnder(bar, smaFast, smaSlow)) //Если же произошло пересечение скользящих сверху вниз тогда   
                    {
                        ExitAtMarket(bar + 1, LastPosition); //Закрываем открытую длинную позицию
                        ShortAtMarket(bar + 1); //Открываем короткую  позицию
                    }
                }
                else //Если мы находимся не в позиции    
                {
                    if (CrossOver(bar, smaFast, smaSlow)) //Проверяем не произошло ли пересечения скользящих снизу вверх если произошло то 
                    {
                        BuyAtMarket(bar + 1); //Открываем длинную  позицию
                    }
                    if (CrossUnder(bar, smaFast, smaSlow)) //Если же произошло пересечение скользящих сверху вниз тогда   
                    {
                        ShortAtMarket(bar + 1); //Открываем короткую  позицию
                    }
                }
            }
        }
    }
}