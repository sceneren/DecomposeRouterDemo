/// This exports this Rust function to the Kotlin side.
mod encryption;
use encryption::decrypt_base_string;

#[uniffi::export]
fn add(lhs: i32, rhs: i32) -> i32 {
    lhs + rhs
}

#[uniffi::export]
fn decrypt_string(encrypted_data:String ) ->String{
   
   return decrypt_base_string(encrypted_data).unwrap();
}

#[uniffi::export]
fn decrypt_string2(encrypted_data:String ) ->String{
    let result= decrypt_base_string(encrypted_data);
    match result {
        Ok(data)=>{
            let a = String::from("success:");
            let b = data;
            return a+&b;
        },
        Err(e)=>{
            let a = String::from("fail:");
            let b = e;
            return a+&b;
        }
    }
 }

// This generates extra Rust code required by UniFFI.
uniffi::setup_scaffolding!();