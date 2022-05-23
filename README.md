Торговый робот для Тинкофф Инвестиций Разработан в рамках Tinkoff Invest Robot Contest 
https://meetup.tinkoff.ru/event/tinkoff-invest-robot-contest/

Конфигурация (.json) 
Exchange - наименование биржи 
Token - токен 
SandboxMode - режим работы: true - sandbox, false - real 
LimitsMoney - лимит по денежным средствам 
Shares - описание акции: 
figi; 
lowPrice, highPrice - сигнальные цены для отркытия позиции в long/short; 
percentageGap - процентный шаг цены, с которым будут открываться позиции; 
percentageProfit - процент для прибыли; 
percentageLost - процент для убытка

Пример конфигурации: 
{ 
  "exchange" : "moex", 
  "token" : "", 
  "sandboxMode" : false, 
  "limitsMoney" : 500000, 
  "shares" : [
     {
      "figi" : "BBG004730ZJ9",
      "lowPrice" : 200,
      "highPrice" : 262,
      "percentageGap" :0.5,
      "percentageProfit" : 1.5,
      "percentageLost" : 1
    }
   ] 
}

Открытие позиции в long/short, когда цена достигает нижнего/верхнего уровня. Фиксация прибыли/убытка при отклонении цены на заданное количество процентов
