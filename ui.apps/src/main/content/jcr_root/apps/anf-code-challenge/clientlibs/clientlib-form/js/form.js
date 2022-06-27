$(document).ready(function() {
    if($('#saveDetails form')){
        $('#saveDetails form').attr('action','javascript:void();');
        $('#saveDetails form').submit(function(){
            if(!$('[name="first-name"]').val() || !$('[name="last-name"]').val() || !$('[name="age"]').val()) {
                alert('Fill all the details before submitting the form');
            } else {
                $.getJSON('/etc/age.json',{},function(data){
                    if(Number(data.minAge) <= Number($('[name="age"]').val()) && Number(data.maxAge) >= Number($('[name="age"]').val())) {
                        $.get('/bin/saveUserDetails',{'fname':$('[name="first-name"]').val(), 'lname':$('[name="last-name"]').val(), 'age':$('[name="age"]').val()},function(data){
                            if(data==="saved") {
                                alert('Saved data');
                            }
                        });
                    } else {
                        alert("Specified age is not supported");
                    }
                });
            }
            return false;
        });
    }
});