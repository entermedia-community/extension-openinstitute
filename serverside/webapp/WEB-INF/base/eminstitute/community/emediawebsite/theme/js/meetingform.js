
$(document).ready(function () {

 $("#meetingtime").on("change", function() {
        var timeSlot = $("#meetingtime option:selected").val();
        if(timeSlot === "custom") {
          $("#customTimeSlot").collapse("show");
          $(this).prop("required", false);
          $("#datetimepicker").prop("required", true);
        } else {
          $("#customTimeSlot").collapse("hide");
          $(this).prop("required", true);
          $("#datetimepicker").prop("required", false);
        }
      });
      $("#datetimepicker").datetimepicker({
        inline: true,
        format: 'Y-m-d H:i:00 O',
        showTimezone: true,
        minDate: new Date(),
        maxDate: new Date(new Date().setDate(new Date().getDate() + 30)),
      });
 });