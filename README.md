# java-explore-with-me

Проект java-explore-with-me представляет собой приложение по
размещению и поиску событий.
Приложение доступно как для зарегестрированных пользователей, так и для не прошедших авторизацию.

Для неавторизованных пользователей доступны:
* поиск событий с определнными критериями пользователя;
* поиск события по id.

Для авторизованного пользователя доступен следующий функционал:
* размещение события, его редактирование и отмена;
* размещение заявки на участие в событии, размещенном другим пользователем;
* различные виды поиска, в том числе поиск событий, которые созданы данным пользователем, поиск оставленных им заявок и др.

Администратор может выполнять следующие действия в приложениее:
* создавать пользователей;
* публиковать, редактировать и отклонять события;
* осуществлять поиск по пользователям и событиям;
* создавать, редактировать и удалять категории и подборки событий;
* закреплять и откреплять подборки событий.

Приложение состоит из двух модулей:
* service;
* statistics.

Первый осуществляяет всю бизнес-логику приложения, второй - собирает и возвращает статистику.

На данный момент в статитстику попадает информация о вызовах публичных методов.

Проект является дипломной работой по курсу Яндекс.Практикум.Java-разработчик.

Проект подготовлен Лукшиной Юлией (ualukshina@yandex.ru)

Ссылка на pull request: https://github.com/julialukshina/java-explore-with-me/pull/1