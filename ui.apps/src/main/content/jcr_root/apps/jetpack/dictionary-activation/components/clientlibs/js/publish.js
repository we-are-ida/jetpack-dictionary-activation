/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
(function(window, document, $, Granite) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");
    var COMMAND_URL = Granite.HTTP.externalize("/bin/wcmcommand");
    var deleteText = Granite.I18n.get("Publish");
    var cancelText = Granite.I18n.get("Cancel");

    function progressTicker(title, message) {
        var el = new Coral.Dialog();
        el.backdrop = Coral.Dialog.backdrop.STATIC;
        el.header.textContent = title;
        el.header.insertBefore(new Coral.Wait(), el.header.firstChild);
        el.content.innerHTML = message || "";

        document.body.appendChild(el);
        el.show();

        return {
            finished: function(message) {
                el.header.textContent = "Finished"
                el.content.innerHTML = message;

                var b = new Coral.Button();
                b.label.textContent = "Close";
                b.variant = "primary";

                b.on("click", function(e) {
                    //ui.clearWait();
                    window.location.reload();
                });

                el.footer.appendChild(b);
            },
            updateMessage: function(message) {
                el.content.innerHTML = message;
            },
            clear: function() {
                el.hide();

                requestAnimationFrame(function() {
                    el.remove();
                });
            }
        };
    }

    function publishDictionaries(paths) {

        var tickerMessage = $(document.createElement("div"));

        var wt = progressTicker("Processing", "Starting publishing ...");

        // creates an anonymous function that executes a publish request for the specified path
        // and returns a promise that resolves after the call has completed (successfully or not)
        function createPublishRequest(path) {
            return function () {

                wt.updateMessage(tickerMessage.html()
                    + path + "&nbsp;&nbsp; [in progress ...]<br/>");

                var deferred = $.Deferred();
                $.ajax({
                    url: COMMAND_URL,
                    type: "POST",
                    data: {
                        _charset_: "UTF-8",
                        cmd: "publishDictionary",
                        path: path
                    }
                }).fail(function() {
                    console.error("Failed to publish", path);
                    $(document.createElement("div"))
                        .html(path + "&nbsp;&nbsp; <b>FAILED</b>")
                        .appendTo(tickerMessage);
                }).done(function() {
                    console.log("Successfully published", path);
                    $(document.createElement("div"))
                        .html(path + "&nbsp;&nbsp; <b>SUCCESS</b>")
                        .appendTo(tickerMessage);
                }).always(function () {
                    deferred.resolve();
                    wt.updateMessage(tickerMessage.html());
                });

                return deferred.promise();
            };
        }

        // show spinner
        //ui.wait();

        // chain publish requests to execute them sequentially to avoid concurrent modifications
        var requests = $.Deferred();
        requests.resolve();
        for (var i = 0; i < paths.length; i++) {
            var path = paths[i];
            requests = requests.then(createPublishRequest(path));
        }

        // hide spinner and reload page after all requests have been executed
        requests.always(function() {
            wt.finished(tickerMessage.html());

            setTimeout(function () {
                //ui.clearWait();
                //window.location.reload();
            }, 3000);
        });
    }

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq-admin.jetpack.dictionary-activation.action.publish",
        handler: function(name, el, config, collection, selections) {
            var message = $(document.createElement("div"));

            var intro = $(document.createElement("p")).appendTo(message);
            if (selections.length === 1) {
                intro.text(Granite.I18n.get("You are going to publish the following dictionary:"));
            } else {
                intro.text(Granite.I18n.get("You are going to publish the following {0} dictionaries:", selections.length));
            }

            var list = [];
            var maxCount = Math.min(selections.length, 12);
            for (var i = 0, ln = maxCount; i < ln; i++) {
                var title = $(selections[i]).find(".main-sync h4").text();
                list.push($("<b>").text(title).html());
            }
            if (selections.length > maxCount) {
                list.push("&#8230;"); // &#8230; is ellipsis
            }

            $(document.createElement("p")).html(list.join("<br>")).appendTo(message);

            ui.prompt(deleteText, message.html(), "notice", [{
                text: cancelText
            }, {
                text: deleteText,
                primary: true,
                handler: function() {
                    var paths = selections.map(function(v) {
                        return $(v).data("path");
                    });

                    publishDictionaries(paths);
                }
            }]);
        }
    });
})(window, document, Granite.$, Granite);