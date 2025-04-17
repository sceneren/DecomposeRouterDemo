
use base64::Engine;
use serde_json;
use serde::{ Deserialize, Serialize};
use libaes::Cipher;


#[derive(Debug, Serialize, Deserialize)]
struct CryptoData {
    iv: String,
    value: String,
}

static  AES_KEY :&str= "3mxTAiHP4cbi3Ij5u8hs3M";

pub fn decrypt_base_string(base64_encode_str:String)->Result<String,String>{
    let base64_decode_str = local_base64_decode(&base64_encode_str).unwrap();
    let crypto_data = local_json_decode(&base64_decode_str).unwrap();
    let iv_str = local_base64_decode(&crypto_data.iv).unwrap();
    println!("{}",iv_str);
    println!("{}",crypto_data.value);
    let cipher = Cipher::new_256(&str_to_u8_32(&AES_KEY));
    let decrypted = cipher.cbc_decrypt(&iv_str.as_bytes(), crypto_data.value.as_bytes());
    let result = String::from_utf8(decrypted).unwrap();
    return Ok(result);
}

fn local_base64_decode(base64_encode_str:&String)-> Result<String, String> {
    let encrypt_str = base64_encode_str.to_string();
    let base64_result =  base64::engine::general_purpose::STANDARD.decode(encrypt_str).unwrap();
    let data_str = String::from_utf8(base64_result);
    match data_str {
        Ok(data)=>{
          return  Ok(data);
        },
        Err(_)=>{
            return Err("base64 Error".to_string());
        }
    }
}

fn local_json_decode(json_str:&String)->Result<CryptoData,String>{
    let crypto_data: Result<CryptoData, serde_json::Error> = serde_json::from_str(json_str);
    match crypto_data {
        Ok(data)=>{
            return Ok(data);
        },
        Err(_)=>{
            return Err("json error".to_string());
        }
    }
   
}

fn str_to_u8_32(input_str: &str) -> [u8; 32] {
    // 1. 创建一个用 0 填充的 32 字节数组。
    let mut output_array = [0u8; 32];

    // 2. 获取字符串的字节表示 (&[u8])。
    let input_bytes = input_str.as_bytes();

    // 3. 计算需要复制的字节数。
    //    取输入字节长度和数组长度 32 中的较小值。
    let len_to_copy = std::cmp::min(input_bytes.len(), 32);
    // let len_to_copy = input_bytes.len().min(32); // 也可以这样写

    // 4. 将计算出的字节数从 input_bytes 复制到 output_array 的开头。
    //    `copy_from_slice` 要求源和目标切片的长度必须完全匹配。
    output_array[..len_to_copy].copy_from_slice(&input_bytes[..len_to_copy]);

    // 5. 返回填充/截断后的数组。
    output_array
}

fn  main() {
    let base64_encode_str = "eyJpdiI6ImNqQnpOVkJHU21kelJURnhkVmQ2V0E9PSIsInZhbHVlIjoiam1IMkZDUWczQ3ZNY2NVUzdxZitZRlZZK1dBUVZcL0Q3ckxzRG4wVkhzU0Q0UWFwUFFVejYyWHIrTXl2NUIyVlEzUEdJR21JQWdJZDR2Sk9qaStKdFFGYXdNSnJZVzAxZElZV2hzWUVkbUVlVlVYaTRhSmZ4KzVNakVUcGN6SWdMbUd0TkdwRm1ETGJYNVFEZ0RiVkVRUm14eUZObUo5eHdla2pUOXVHVnpFYk1ZMlJcL2dwVjV0RldhZzFUM0I4d3c2dDlnN3BIcGtCS2VDeGVjRklsYTE5clhxaWNxSHNPMHU0NmdVSlorNG1DcEtIdzFFRFBnbkdBazZZN3FOWHBhUk1uM1FjT3FcL1RcL1l5ckRqVW1rcFhzeHhwWnNuaFBcL2owS1NSK0wwQWdBSkZBZWZ1cmxtRWtQMTBVcVJyTHlXZjI5U1A3Q3BQUGkrTTdMa0FHZEoySlVcL1FqRWFzaEN2cGdndEtFQ2pSQXZ4Q3JMdzJPNnl4dFdxek42K0pXaG0zVGpcL2hOS0N1OW5ENmM4dEQrZHBadVo5RHlRR1JGNnZvMWlFbGJxSld2aWtwQXR2M1l4NVlSUEYxenV6WWE4NnFEYXg4RzF4N3FkeG9LTGQxUW9kWEp6UjNkSXNoMHJvRmw0WTJocjdESTFSUFd0MmZwREFJYnJDWFUzVzh6WDhpVVY3ZjNqdlRHU2ZJUE1kMm51MzBJQVB2Q05xWHpFY1wvNDY3bXFzRmpROGtaZU9CM1JHN1VlcGIyVHFqR0tqU2RoYVwvSTVoblhlRGJxeDVDbUtzQzUyd0tWalVSYWtJRHZseDJ5SFZuWWZHSzdzZXRLRytrRVQwZkpWU3diek80NjVWZ3I2TUNEbmJmSkg0NHZEclc2MlhWXC92WmZ0SzBNY2dvUWdFRzd2Y0d1RldCaEFaV1ZDT29NMndNdmNnRUdaUXgybUVmSnZtT0tFejJmbUJTandYZWFyQjVFNUk2U21MckN6aHM4YWpOMk1PdkluczFxcFJza05EeXYxZTJES2Y3cmRadGlvMit3c1BNcEtNdXBjM2dqZUFyM2tObkpDUllLUFdRcU1haE1OU3JJTUVZblQyMG1KTEpuZjFQQlQxQldCMjF0akJMV2tKTk01TUd4OWx3N3k3bzBJd3pZdjNTMW5PeXpJNzJhcGxZNGQ2VFFSVzd6SGtPcWJBZklod1Vjb0VyZVMzWG1BcFlOK1RFd1MyVjZMd3F4a3RoZDR3RWZ4T3NybjE2a3kxcDZ4QUpVaFZuZDNnOVhDcHlQN09IMTZ2ejBqa2wrUHZvdUY3ekE3ZitaV0ZLdHFlVXhcL21OWkYyOVdFdFI4PSJ9".to_string();
    let result = decrypt_base_string(base64_encode_str).unwrap();
    print!("{}",result);
}