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
    makeSortable("exam")
    makeSortable("question")

});

function makeSortable(name) {
    $("." + name + "-list").sortable({
            update: updateSortedList(name)
        }
    );
}

function updateSortedList(name) {
    return function (event, ui) {
        var id = $(this).attr("parentId");
        var order = $(this).sortable("toArray", {attribute: "itemId"});

        console.info(id, order);

        $.ajax({
            type: "POST",
            url: "/" + name + "/arrange/" + id,
            data: JSON.stringify({order: order}),
            contentType: "application/json"
        });
    }
}


function updateQuestionsIndex(event, ui) {
    var examId = $("#exam").attr("examId");
    var order = $('#questions-list').sortable("toArray", {attribute: "questionId"});

    console.info(examId, order)

    $.ajax({
        type: "POST",
        url: "/exam/arrageQuestions/" + examId,
        data: JSON.stringify({order: order}),
        contentType: "application/json"
    });
}


function makeSortables() {
    /*  $('.exam-list').sortable({
     update: updateQuestionsIndex
     }
     );*/
}

makeSortable("exam")