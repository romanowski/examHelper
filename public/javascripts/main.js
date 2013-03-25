/*
 function fixTabs() {
 var baseId = window.location.hash;
 $(".tabButton").removeClass("active")
 $(baseId + "-button").addClass('active')
 console.info(baseId + "-button")
 }

 $(function () {
 $(".tabButton").each(function (id) {
 var b = $(this)
 if (b.attr('href'))
 b.attr("id", b.attr('href').substring(1) + "-button")
 })
 fixTabs();
 });
 $(window).on('hashchange', fixTabs);*/


function fixHashParams(paramName, paramValue) {
    $(".hash-" + paramName).each(function () {
        var sel = $(this)
        sel.attr(paramName, sel.attr(paramName).replace(/#\w+/, "") + '#' + paramValue)
    });
}

function fixHash(hash) {
    location.hash = hash;
    fixHashParams("action", hash)
    fixHashParams("href", hash)
}

$(function () {
    $('#exam-tabs a').click(function (e) {
        fixHash($(this).attr('href').substr(1))
    });
    fixHash(location.hash.substr(1))
    $(location.hash + "-button").tab("show")
})

